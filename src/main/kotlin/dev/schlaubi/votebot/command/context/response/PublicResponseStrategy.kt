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

import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.PublicInteractionResponseBehavior
import dev.kord.core.behavior.interaction.edit
import dev.kord.core.behavior.interaction.followUp
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.interaction.PublicFollowupMessageCreateBuilder
import dev.kord.rest.builder.message.MessageCreateBuilder
import dev.kord.rest.builder.message.MessageModifyBuilder

internal class PublicResponseStrategy(private val ack: PublicInteractionResponseBehavior) :
    FollowUpResponseStrategy(),
    Responder {
    override suspend fun internalFollowUp(builder: PublicFollowupMessageCreateBuilder.() -> Unit): PublicFollowupMessage =
        ack.followUp(builder)

    override suspend fun respond(builder: MessageCreateBuilder.() -> Unit): Responder.EditableResponse {
        val message = MessageCreateBuilder().apply(builder)
        val response = ack.edit {
            content = message.content
            message.embed?.let {
                embeds.add(it)
            }
            allowedMentions = message.allowedMentions
            message.files.forEach { (name, inputStream) ->
                addFile(name, inputStream)
            }
        }

        return MessageEditableResponse(response)
    }

    private class MessageEditableResponse(private val sentMessage: Message) : Responder.EditableResponse {
        override suspend fun edit(builder: MessageModifyBuilder.() -> Unit): Responder.EditableResponse {
            val message = MessageModifyBuilder().apply(builder)
            sentMessage.edit {
                content = message.content
                embed = message.embed
                allowedMentions = message.allowedMentions
            }

            return this
        }
    }
}
