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

package dev.schlaubi.votebot.commands

import dev.schlaubi.votebot.command.SingleCommand
import dev.schlaubi.votebot.command.context.Context
import dev.schlaubi.votebot.command.context.response.respond

object InfoCommand : SingleCommand() {
    override val description: String = "Displays basic information about the bot"
    override val name: String = "info"
    override val useEphemeral: Boolean = true

    override suspend fun execute(context: Context) {
        context.respond("Coming soon :tm:")
    }
}
