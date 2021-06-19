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

import dev.kord.rest.builder.interaction.SubCommandBuilder
import dev.schlaubi.votebot.command.RootCommand
import dev.schlaubi.votebot.command.SubCommand
import dev.schlaubi.votebot.command.context.Context
import dev.schlaubi.votebot.core.VoteBotBundle
import dev.schlaubi.votebot.util.addCommand
import dev.schlaubi.votebot.util.buildCommands

object CreateCommand : RootCommand(
    buildCommands {
        addCommand(MultiOptionsCommand)
        addCommand(SingleOptionsCommand)
    }
) {
    override val name: String = "create"
    override val description: String = VoteBotBundle.getMessage("commands.create.description")

    private abstract class VoteCreationCommand : SubCommand() {

        open fun SubCommandBuilder.addAdditionalArguments() = Unit

        final override fun SubCommandBuilder.addArguments() {
            string("options", VoteBotBundle.getMessage("commands.create.arguments.options.description"))
            addAdditionalArguments()
        }
    }

    private object MultiOptionsCommand : VoteCreationCommand() {
        override val name: String = "multi"
        override val description: String =
            VoteBotBundle.getMessage("commands.create.multi.description")

        override fun SubCommandBuilder.addAdditionalArguments() {

        }

        override suspend fun execute(context: Context) {
            TODO("Not yet implemented")
        }
    }

    private object SingleOptionsCommand : VoteCreationCommand() {
        override val name: String = "multi"
        override val description: String =
            VoteBotBundle.getMessage("commands.create.single.description")

        override suspend fun execute(context: Context) {
            TODO("Not yet implemented")
        }
    }
}
