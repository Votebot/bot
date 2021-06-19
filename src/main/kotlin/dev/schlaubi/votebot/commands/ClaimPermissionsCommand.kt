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

import dev.kord.common.entity.Permission
import dev.schlaubi.votebot.command.SingleCommand
import dev.schlaubi.votebot.command.context.Context
import dev.schlaubi.votebot.command.context.response.respond
import dev.schlaubi.votebot.util.appendPermission
import dev.schlaubi.votebot.util.getCommands
import kotlinx.coroutines.flow.first

object ClaimPermissionsCommand : SingleCommand() {
    override val name: String = "claim-permissions"
    override val useEphemeral: Boolean = true
    override val description: String =
        "Allows you to claim /permissions command permissions if you have ADMINISTRATOR permissions"

    override suspend fun execute(context: Context) {
        if (Permission.Administrator !in context.executor.asMember().getPermissions()) {
            context.respond("You need the `ADMINISTRATOR` permission to execute this command")
            return
        }

        val command = context.guild.getCommands().first { it.name == PermissionCommand.name }

        appendPermission(
            command.id,
            context.bot.kord,
            context.guild.id
        ) {
            user(context.executor.id, true)
        }

        context.respond("You now have access to /permissions")
    }
}
