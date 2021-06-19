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

import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.optional.Optional
import dev.kord.core.behavior.interaction.EphemeralInteractionResponseBehavior
import dev.kord.core.behavior.interaction.followUp
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.interaction.PublicFollowupMessageCreateBuilder
import dev.kord.rest.builder.message.MessageCreateBuilder
import dev.kord.rest.json.request.InteractionResponseModifyRequest
import dev.schlaubi.votebot.util.optional

internal class EphemeralResponseStrategy(private val ack: EphemeralInteractionResponseBehavior) :
    FollowUpResponseStrategy(), Responder {
    override suspend fun respond(builder: MessageCreateBuilder.() -> Unit): Responder.EditableResponse {
        val message = MessageCreateBuilder().apply(builder)
        val embeds = listOfNotNull(message.embed?.toRequest())
        val request = InteractionResponseModifyRequest(
            message.content.optional(),
            Optional.missingOnEmpty(embeds),
            message.allowedMentions?.build().optional()
        )

        ack.kord.rest.interaction.modifyInteractionResponse(ack.applicationId, ack.token, request)

        return NonEditableResponse
    }

    @OptIn(KordUnsafe::class)
    override suspend fun internalFollowUp(builder: PublicFollowupMessageCreateBuilder.() -> Unit): PublicFollowupMessage =
        ack.followUp(builder)
}
