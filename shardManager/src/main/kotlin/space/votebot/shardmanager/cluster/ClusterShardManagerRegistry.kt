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

package space.votebot.shardmanager.cluster

import mu.KotlinLogging

class ClusterShardManagerRegistry {

    private val log = KotlinLogging.logger {}
    private val shardManagerList = mutableListOf<ClusterShardManager>()

    fun registerShardManager(shardManager: ClusterShardManager): Boolean {
        if (shardManagerList.stream().anyMatch { it.address == shardManager.address && it.port == shardManager.port }) {
            return false
        }
        shardManagerList.add(shardManager)
        log.info { "Registered shardManager: ${shardManager.address}:${shardManager.port}" }
        return true
    }
}