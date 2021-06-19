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

package dev.schlaubi.votebot.util

import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.interaction.ApplicationCommand
import dev.schlaubi.votebot.command.DescriptiveCommand
import dev.schlaubi.votebot.config.Config
import dev.schlaubi.votebot.config.Environment
import kotlinx.coroutines.flow.Flow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Retrieves the commands depending on [Environment.useGlobalCommands].
 */
fun GuildBehavior.getCommands(): Flow<ApplicationCommand> = if (Config.ENVIRONMENT.useGlobalCommands) {
    kord.slashCommands.getGlobalApplicationCommands()
} else {
    kord.slashCommands.getGuildApplicationCommands(id)
}

/**
 * Builder for a command-name-map.
 */
@OptIn(ExperimentalContracts::class)
inline fun <C : DescriptiveCommand> buildCommands(builder: MutableMap<String, C>.() -> Unit): Map<String, C> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return buildMap(builder)
}

/**
 * Adds a command to a command-name-map
 *
 * @see buildCommands
 */
fun <C : DescriptiveCommand> MutableMap<String, C>.addCommand(command: C) {
    this[command.name] = command
}
