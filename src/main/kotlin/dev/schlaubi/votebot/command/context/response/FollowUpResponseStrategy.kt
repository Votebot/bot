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

import dev.kord.core.behavior.interaction.edit
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.interaction.PublicFollowupMessageCreateBuilder
import dev.kord.rest.builder.message.MessageCreateBuilder
import dev.kord.rest.builder.message.MessageModifyBuilder

internal sealed class FollowUpResponseStrategy : Responder {
    protected abstract suspend fun internalFollowUp(builder: PublicFollowupMessageCreateBuilder.() -> Unit): PublicFollowupMessage

    final override suspend fun followUp(builder: MessageCreateBuilder.() -> Unit): Responder.EditableResponse {
        val message = MessageCreateBuilder().apply(builder)
        val response = internalFollowUp {
            content = message.content
            message.embed?.let {
                embeds.add(it)
            }
            allowedMentions = message.allowedMentions
            files.addAll(message.files)
        }

        return PublicFollowUpEditableResponse(response)
    }

    private class PublicFollowUpEditableResponse(private val response: PublicFollowupMessage) :
        Responder.EditableResponse {
        override suspend fun edit(builder: MessageModifyBuilder.() -> Unit): Responder.EditableResponse {
            val message = MessageModifyBuilder().apply(builder)
            response.edit {
                content = message.content
                message.embed?.let {
                    embeds.add(it)
                }
                allowedMentions = message.allowedMentions
            }

            return this
        }
    }
}
