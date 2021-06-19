/*
 * VoteBot - A feature-rich bot to create votes on Discord guilds.
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

package dev.schlaubi.votebot.commands

import dev.kord.rest.builder.interaction.ApplicationCommandPermissionsModifyBuilder
import dev.kord.rest.builder.interaction.SubCommandBuilder
import dev.schlaubi.votebot.command.RootCommand
import dev.schlaubi.votebot.command.SubCommand
import dev.schlaubi.votebot.command.context.Context
import dev.schlaubi.votebot.command.context.response.respond
import dev.schlaubi.votebot.config.Config
import dev.schlaubi.votebot.util.addCommand
import dev.schlaubi.votebot.util.appendPermission
import dev.schlaubi.votebot.util.buildCommands
import kotlinx.coroutines.flow.firstOrNull

object PermissionCommand : RootCommand(
    buildCommands {
        addCommand(RoleCommand())
        addCommand(UserCommand())
    }
) {
    override val name: String = "permissions"
    override val defaultPermission: Boolean = false
    override val description: String = "Allows you to manage the bots permissions"

    class RoleCommand : SubCommand() {
        override val name: String = "role"
        override val useEphemeral: Boolean = true
        override val description: String = "Allows you to manage the permissions of a role"

        override fun SubCommandBuilder.addArguments() {
            commandArgument()
            role("role", "The Role you want to change the permissions of") {
                required = true
            }
            permissionArgument()
        }

        override suspend fun execute(context: Context) {
            doPermissions(context) { permission ->
                role(context.role("role").id, permission)
            }
        }
    }

    class UserCommand : SubCommand() {
        override val name: String = "user"
        override val useEphemeral: Boolean = true
        override val description: String = "Allows you to manage the permissions of a user"

        override fun SubCommandBuilder.addArguments() {
            commandArgument()
            user("user", "The User you want to change the permissions of") {
                required = true
            }
            permissionArgument()
        }

        override suspend fun execute(context: Context) {
            doPermissions(context) { permission ->
                user(context.user("user").id, permission)
            }
        }
    }
}

private fun SubCommandBuilder.permissionArgument() {
    boolean("permission", "Whether you want to allow using the command or not") {
        required = true
    }
}

private fun SubCommandBuilder.commandArgument() {
    string("command", "The command you want to change the permissions for") {
        required = true
    }
}

suspend fun doPermissions(
    context: Context,
    addPermission: ApplicationCommandPermissionsModifyBuilder.(Boolean) -> Unit
) {
    val commandName = context.string("command")
    val commands = if (Config.ENVIRONMENT.useGlobalCommands) {
        context.bot.kord.slashCommands.getGlobalApplicationCommands()
    } else {
        context.bot.kord.slashCommands.getGuildApplicationCommands(context.guild.id)
    }

    val command = commands.firstOrNull { it.name == commandName }
    if (command == null) {
        context.respond("Unknown command")
        return
    }

    appendPermission(
        command.id,
        context.bot.kord,
        context.guild.id
    ) {
        addPermission(context.boolean("permission"))
    }

    context.respond("Permission was updated!")
}
