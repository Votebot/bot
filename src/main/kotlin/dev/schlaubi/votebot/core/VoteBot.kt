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

import dev.kord.core.Kord
import dev.schlaubi.votebot.command.RegistrableCommand
import kotlinx.coroutines.CoroutineScope

/**
 * VoteBot monolith I guess?
 */
interface VoteBot : CoroutineScope {

    /**
     * Kord.
     */
    val kord: Kord

    /**
     * A list of all registered commands.
     */
    val commands: Map<String, RegistrableCommand>
}
