/*
 * Votebot - A feature-rich bot to create votes on Discord guilds.
 *
 * Copyright (C) 2019-2021  Michael Rittmeister & Yannick Seeger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package dev.schlaubi.votebot.command.internal

import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.interaction.GuildInteraction
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.ApplicationCommandsCreateBuilder
import dev.schlaubi.votebot.command.CommandErrorHandler
import dev.schlaubi.votebot.command.ExecutableCommand
import dev.schlaubi.votebot.command.RegistrableCommand
import dev.schlaubi.votebot.command.RootCommand
import dev.schlaubi.votebot.command.SingleCommand
import dev.schlaubi.votebot.command.context.response.EphemeralResponseStrategy
import dev.schlaubi.votebot.command.context.response.PublicResponseStrategy
import dev.schlaubi.votebot.command.context.response.Responder
import dev.schlaubi.votebot.config.Config
import dev.schlaubi.votebot.core.VoteBot
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

private val LOG = KotlinLogging.logger { }

class CommandExecutor(
    private val bot: VoteBot,
    val commands: Map<String, RegistrableCommand>,
    private val errorHandler: CommandErrorHandler
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = bot.coroutineContext + SupervisorJob()
    private val listener = bot.kord.on<InteractionCreateEvent> { handle() }

    private suspend fun InteractionCreateEvent.handle() {
        val interactionCommand = interaction.command
        if (interaction !is GuildInteraction) {
            interaction.respondEphemeral("You cannot use this bot in your DMs")
            return
        }

        val command = commands[interactionCommand.rootName] ?: return

        val foundCommand: ExecutableCommand = if (command is RootCommand) {
            val subCommand = (interactionCommand as? SubCommand)
                ?: error("Registered command isn't sub command but implementation is. Class: ${command::class.qualifiedName}")

            command.subCommands[subCommand.name]
                ?: error("Could not find subcommand: ${subCommand.name} for root ${interactionCommand.rootName}")
        } else command as SingleCommand

        val responseStrategy: Responder = if (foundCommand.useEphemeral) {
            EphemeralResponseStrategy(interaction.acknowledgeEphemeral())
        } else {
            PublicResponseStrategy(interaction.ackowledgePublic())
        }

        val context = ContextImpl(
            bot,
            foundCommand,
            responseStrategy,
            this
        )

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            LOG.error(throwable) { "Error whilst executing command" }
            launch {
                errorHandler.handleCommandError(context, coroutineContext, throwable)
            }
        }

        LOG.debug { "Command ${command.name} was executed by ${context.interaction.user.id}" }
        launch(exceptionHandler) {
            foundCommand.execute(context)
        }
    }

    suspend fun updateCommand() {
        val commandRegisterer: ApplicationCommandsCreateBuilder.() -> Unit = {
            this@CommandExecutor.commands.forEach { (_, command) ->
                with(command) {
                    command(command.name, command.description) {
                        defaultPermission = command.defaultPermission
                        addArguments()
                    }
                }
            }
        }

        if (Config.ENVIRONMENT.useGlobalCommands) {
            bot.kord.slashCommands.createGlobalApplicationCommands(commandRegisterer).launchIn(bot)
        } else {
            bot.kord.slashCommands.createGuildApplicationCommands(Config.DEV_GUILD!!, commandRegisterer).launchIn(bot)
        }
    }

    fun close() = listener.cancel()
}
