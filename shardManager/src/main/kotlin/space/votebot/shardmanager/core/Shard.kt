/*
 * VoteBot
 * Copyright (C) 2019 VoteBot
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package space.votebot.shardmanager.core

import space.votebot.shardmanager.api.Shard

data class Shard(
        val address: String,
        val port: Int,
        val status: Status,
        val leader: String
) {

    enum class Status {
        IDLE,
        SERVING
    }

    fun toProtoShard() = Shard.newBuilder()
            .setAddress(address)
            .setPort(port)
            .setStatus(Shard.Status.valueOf(status.name))
            .setLeader(leader)
            .build()
}