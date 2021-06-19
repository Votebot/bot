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

package dev.schlaubi.votebot.command.context

import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.interaction.OptionValue

/**
 * Container for command options.
 */
interface Arguments {
    /**
     * Returns an nullable [String] for [name].
     */
    fun optionalString(name: String): String? = optionalArgument(name)?.cast<String>()?.value

    /**
     * Returns a non-nullable [String] for [name].
     */
    fun string(name: String): String = argument(name).cast<String>().value

    /**
     * Returns an nullable [Int] for [name].
     */
    fun optionalInt(name: String): Int? = optionalArgument(name)?.cast<Int>()?.value

    /**
     * Returns a non-nullable [Int] for [name].
     */
    fun int(name: String): Int = argument(name).cast<Int>().value

    /**
     * Returns an nullable [Boolean] for [name].
     */
    fun optionalBoolean(name: String): Boolean? = optionalArgument(name)?.cast<Boolean>()?.value

    /**
     * Returns a non-nullable [Boolean] for [name].
     */
    fun boolean(name: String): Boolean = argument(name).cast<Boolean>().value

    /**
     * Returns an nullable [User] for [name].
     */
    fun optionalUser(name: String): User? = optionalArgument(name)?.cast<User>()?.value

    /**
     * Returns a non-nullable [User] for [name].
     */
    fun user(name: String): User = argument(name).cast<User>().value

    /**
     * Returns an nullable [Member] for [name].
     */
    fun optionalMember(name: String): Member? = optionalArgument(name)?.cast<Member>()?.value

    /**
     * Returns a non-nullable [Member] for [name].
     */
    fun member(name: String): Member = argument(name).cast<Member>().value

    /**
     * Returns an nullable [Role] for [name].
     */
    fun optionalRole(name: String): Role? = optionalArgument(name)?.cast<Role>()?.value

    /**
     * Returns a non-nullable [Role] for [name].
     */
    fun role(name: String): Role = argument(name).cast<Role>().value

    /**
     * Returns an nullable [GuildChannel] for [name].
     */
    fun optionalChannel(name: String): GuildChannel? = optionalArgument(name)?.cast<GuildChannel>()?.value

    /**
     * Returns a non-nullable [GuildChannel] for [name].
     */
    fun channel(name: String): GuildChannel = argument(name).cast<GuildChannel>().value

    /**
     * Finds an nullable [OptionValue] for name.
     */
    fun optionalArgument(name: String): OptionValue<*>?
    private fun argument(name: String): OptionValue<*> =
        optionalArgument(name) ?: error("Could not find argument for name: $name")
    @Suppress("UNCHECKED_CAST")
    private fun <T> OptionValue<*>.cast(): OptionValue<T> = this as OptionValue<T>
}
