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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.litote.kmongo.Id
import org.litote.kmongo.newId

/**
 * Creates an empty vote with default values.
 * @see MongoVote
 */
@Suppress("FunctionName")
fun EmptyVote(
    id: Id<MongoVote> = newId(),
    guildId: Long,
    originalMessage: BaseVote.VoteMessage,
    author: Long,
    heading: String,
    options: List<String>,
    answers: List<BaseVote.Answer> = emptyList(),
    voteChanges: Map<Long, Int> = emptyMap(),
    maximumVotes: Int = 1,
    maximumChanges: Int = 0,
    createdAt: Instant = Clock.System.now()
) = MongoVote(
    id,
    guildId,
    listOf(originalMessage),
    author,
    heading,
    options,
    answers,
    voteChanges,
    maximumVotes,
    maximumChanges,
    createdAt
)
