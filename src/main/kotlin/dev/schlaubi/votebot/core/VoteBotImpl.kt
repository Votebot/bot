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

package dev.schlaubi.votebot.core

import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.schlaubi.votebot.command.RegistrableCommand
import dev.schlaubi.votebot.command.internal.CommandExecutor
import dev.schlaubi.votebot.commands.ClaimPermissionsCommand
import dev.schlaubi.votebot.commands.InfoCommand
import dev.schlaubi.votebot.commands.PermissionCommand
import dev.schlaubi.votebot.config.Config
import dev.schlaubi.votebot.util.addCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

internal class VoteBotImpl : VoteBot {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    override lateinit var kord: Kord
    private lateinit var commandExecutor: CommandExecutor
    override val commands: Map<String, RegistrableCommand>
        get() = commandExecutor.commands

    suspend fun start() {
        kord = Kord(Config.DISCORD_TOKEN)
        commandExecutor = CommandExecutor(
            this,
            commands(),
            Config.ENVIRONMENT.errorHandler
        )
        commandExecutor.updateCommand()
        kord.login()
    }

    private fun commands() = buildMap<String, RegistrableCommand> {
        addCommand(InfoCommand)
        addCommand(PermissionCommand)
        addCommand(ClaimPermissionsCommand)
    }
}
