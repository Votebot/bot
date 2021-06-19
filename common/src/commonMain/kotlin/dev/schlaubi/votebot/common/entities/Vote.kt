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

package dev.schlaubi.votebot.common.entities

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * ID type independent vote representation.
 *
 * @param ID the type of the vote id
 * @property id the id of the vote
 * @property guildId the id of the guild the vote is on
 * @property messageIds a map storing the messages representing a status of the vote
 * @property author the id of the author of the vote
 * @property heading the heading of the vote
 * @property options a list of options for the vote
 * @property answers a list of [Answers][Answer]
 * @property voteChanges a map storing the amount of times a user changed it's votes (voteId -> amount)
 * @property maximumVotes the maximum amount of votes a user can give
 * @property maximumChanges the maximum amount of times a user can change his votes (total amount)
 * @property createdAt the [Instant] in which the vote got created
 */
public interface BaseVote<ID> {
    public val id: ID
    public val guildId: Long
    public val messageIds: List<VoteMessage>
    public val author: Long
    public val heading: String
    public val options: List<String>
    public val answers: List<Answer>
    public val voteChanges: Map<Long, Int>
    public val maximumVotes: Int
    public val maximumChanges: Int
    public val createdAt: Instant

    /**
     * Converts this vote into a [Vote].
     */
    public fun toVote(): Vote

    /**
     * A message showing the votes live status.
     *
     * @property textChannel the text channel in which the message was sent in
     * @property messageId the id of the message
     */
    @Serializable
    public data class VoteMessage(val textChannel: Long, val messageId: Long)

    /**
     * An user answer to a vote.
     *
     * @property user the id of the user who gave the answer.
     * @property answers a list of answer indexes the user voted for
     */
    @Serializable
    public data class Answer(
        val user: Long,
        val answers: List<Int>
    )
}

/**
 * Implementation of [BaseVote] using [String] ids.
 */
@Serializable
public data class Vote(
    override val id: String,
    override val guildId: Long,
    override val messageIds: List<BaseVote.VoteMessage>,
    override val author: Long,
    override val heading: String,
    override val options: List<String>,
    override val answers: List<BaseVote.Answer>,
    override val voteChanges: Map<Long, Int>,
    override val maximumVotes: Int,
    override val maximumChanges: Int,
    override val createdAt: Instant
) : BaseVote<String> {
    override fun toVote(): Vote = this
}
