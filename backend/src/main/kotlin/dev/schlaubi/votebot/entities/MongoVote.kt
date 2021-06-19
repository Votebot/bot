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

package dev.schlaubi.votebot.entities

import dev.schlaubi.votebot.common.entities.BaseVote
import dev.schlaubi.votebot.common.entities.Vote
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

/**
 * Implementation of [BaseVote] using an mongodb object id for ids.
 */
@Serializable
data class MongoVote(
    override val id: Id<MongoVote> = newId(),
    override val guildId: Long,
    override val messageIds: List<BaseVote.VoteMessage>,
    override val author: Long,
    override val heading: String,
    override val options: List<String>,
    override val answers: List<BaseVote.Answer>,
    override val voteChanges: Map<Long, Int>,
    override val maximumVotes: Int,
    override val maximumChanges: Int,
    override val createdAt: Instant = Clock.System.now()
) : BaseVote<Id<MongoVote>> {
    override fun toVote() = Vote(
        id.toString(),
        guildId,
        messageIds,
        author,
        heading,
        options,
        answers,
        voteChanges,
        maximumVotes,
        maximumChanges,
        createdAt
    )
}
