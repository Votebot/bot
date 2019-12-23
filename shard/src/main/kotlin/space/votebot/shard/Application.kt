/*
 * VoteBot - ControlPlane
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

package space.votebot.shard

import org.slf4j.LoggerFactory
import space.votebot.common.ConsulRegistry
import space.votebot.shardmanager.api.ShardManagerAPI

fun main() {
    val serviceRegistry = ConsulRegistry("shard", "localhost", 8500)
    serviceRegistry.register(5051)
    LoggerFactory.getLogger("Test").info(serviceRegistry.getService(ShardManagerAPI.SERVICE_ID).port.toString())
}