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

import mu.KotlinLogging

class ShardRegistry {

    private val log = KotlinLogging.logger {}
    val shards = mutableListOf<Shard>()

    fun registerShard(shard: Shard): Boolean {
        if (shards.stream().anyMatch { it.address == shard.address && it.port == shard.port }) {
            return false
        }
        shards.add(shard)
        log.info { "Registered shard: ${shard.address}:${shard.port}" }
        return true
    }
}