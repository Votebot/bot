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

package dev.schlaubi.votebot.command.context.response

import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.MessageCreateBuilder
import dev.kord.rest.builder.message.MessageModifyBuilder
import dev.schlaubi.votebot.command.ExecutableCommand
/**
 * Responding logic for a command execution.
 *
 * @see ExecutableCommand.useEphemeral
 */
interface Responder {
    /**
     * Responds to the command by editing the original ack.
     */
    suspend fun respond(builder: MessageCreateBuilder.() -> Unit): EditableResponse

    /**
     * Sends a follow-up in the command thread.
     */
    suspend fun followUp(builder: MessageCreateBuilder.() -> Unit): EditableResponse

    /**
     * Editing logic for a sent response.
     */
    sealed interface EditableResponse {
        /**
         * Edits the response.
         */
        suspend fun edit(builder: MessageModifyBuilder.() -> Unit): EditableResponse
    }
}

/**
 * Responds to the command by editing the original ack.
 */
suspend inline fun Responder.respond(message: String): Responder.EditableResponse = respond {
    content = message
}

/**
 * Responds to the command by editing the original ack.
 */
suspend inline fun Responder.respond(embed: EmbedBuilder): Responder.EditableResponse = respond {
    this.embed = embed
}

/**
 * Responds to the command by editing the original ack.
 */
suspend inline fun Responder.respondEmbed(crossinline embed: EmbedBuilder.() -> Unit): Responder.EditableResponse =
    respond {
        embed(embed)
    }

/**
 * Sends a follow-up in the command thread.
 */
suspend inline fun Responder.followUp(message: String): Responder.EditableResponse = followUp {
    content = message
}

/**
 * Sends a follow-up in the command thread.
 */
suspend inline fun Responder.followUp(embed: EmbedBuilder): Responder.EditableResponse = followUp {
    this.embed = embed
}

/**
 * Sends a follow-up in the command thread.
 */
suspend inline fun Responder.followUpEmbed(crossinline embed: EmbedBuilder.() -> Unit): Responder.EditableResponse =
    followUp {
        embed(embed)
    }

/**
 * Edits the response.
 */
suspend inline fun Responder.EditableResponse.edit(message: String): Responder.EditableResponse = edit {
    content = message
}

/**
 * Edits the response.
 */
suspend inline fun Responder.EditableResponse.edit(embed: EmbedBuilder): Responder.EditableResponse =
    edit {
        this.embed = embed
    }

/**
 * Edits the response.
 */
suspend inline fun Responder.EditableResponse.edit(crossinline embed: EmbedBuilder.() -> Unit): Responder.EditableResponse =
    edit {
        embed(embed)
    }
