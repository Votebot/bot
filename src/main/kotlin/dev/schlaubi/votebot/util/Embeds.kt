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

package dev.schlaubi.votebot.util

import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.EmbedBuilder

/**
 * Defines a creator of an embed.
 */
typealias EmbedCreator = EmbedBuilder.() -> Unit

/**
 * Some presets for frequently used embeds.
 */
@Suppress("unused", "TooManyFunctions")
object Embeds {

    /**
     * Creates a info embed with the given [title] and [description] and applies the [builder] to it.
     * @see EmbedCreator
     * @see EmbedBuilder
     */
    fun info(title: String, description: String? = null, builder: EmbedCreator = {}): EmbedBuilder =
        embed {
            title(Emotes.INFO, title)
            this.description = description
            color = Colors.BLUE
        }.apply(builder)

    /**
     * Creates a success embed with the given [title] and [description] and applies the [builder] to it.
     * @see EmbedCreator
     * @see EmbedBuilder
     */
    fun success(
        title: String,
        description: String? = null,
        builder: EmbedCreator = {}
    ): EmbedBuilder =
        embed {
            title(Emotes.SUCCESS, title)
            this.description = description
            color = Colors.GREEN
        }.apply(builder)

    /**
     * Creates a error embed with the given [title] and [description] and applies the [builder] to it.
     * @see EmbedCreator
     * @see EmbedBuilder
     */
    fun error(
        title: String,
        description: String? = null,
        builder: EmbedCreator = {}
    ): EmbedBuilder =
        embed {
            title(Emotes.ERROR, title)
            this.description = description
            color = Colors.RED
        }.apply(builder)

    /**
     * Creates a warning embed with the given [title] and [description] and applies the [builder] to it.
     * @see EmbedCreator
     * @see EmbedBuilder
     */
    fun warn(
        title: String,
        description: String? = null,
        builder: EmbedCreator = {}
    ): EmbedBuilder =
        embed {
            title(Emotes.WARN, title)
            this.description = description
            color = Colors.YELLOW
        }.apply(builder)

    /**
     * Creates a loading embed with the given [title] and [description] and applies the [builder] to it.
     * @see EmbedCreator
     * @see EmbedBuilder
     */
    fun loading(
        title: String,
        description: String?,
        builder: EmbedCreator = {}
    ): EmbedBuilder =
        embed {
            title(Emotes.LOADING, title)
            this.description = description
            color = Colors.DARK_BUT_NOT_BLACK
        }.apply(builder)

    private fun EmbedBuilder.title(emote: String, title: String) {
        this.title = "$emote $title"
    }

    /**
     * Sends a new message in this channel containing the embed provided by [base] and applies [creator] to it
     */
    suspend fun MessageChannelBehavior.createEmbed(
        base: EmbedBuilder,
        creator: suspend EmbedBuilder.() -> Unit = {}
    ): Message {
        return createMessage {
            creator(base)
            embed = base
        }
    }

    /**
     * Sends a new message in this channel containing the embed provided by [base] and applies [creator] to it
     */
    suspend fun MessageBehavior.editEmbed(
        base: EmbedBuilder,
        creator: suspend EmbedBuilder.() -> Unit = {}
    ): Message {
        return edit {
            creator(base)
            embed = base
        }
    }
}
