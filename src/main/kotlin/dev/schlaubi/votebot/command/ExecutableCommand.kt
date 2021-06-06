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

package dev.schlaubi.votebot.command

import dev.schlaubi.votebot.command.context.Context

/**
 * A command which has execution logic.
 */
sealed interface ExecutableCommand {

    /**
     * Whether [Context.respond] functions should use ephemeral responses or not.
     */
    val useEphemeral: Boolean
        get() = false

    /**
     * Executes the command logic in [context],
     */
    suspend fun execute(context: Context)
}