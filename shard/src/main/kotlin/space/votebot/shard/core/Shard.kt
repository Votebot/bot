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

package space.votebot.shard.core

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.slf4j.LoggerFactory
import space.votebot.common.ConsulRegistry
import space.votebot.shard.config.Config
import java.util.concurrent.ThreadLocalRandom

class Shard(private val cfg: Config) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val channel = getShardManagerGRPCChannel()

    private fun getShardManagerGRPCChannel(): ManagedChannel {
        val host: String
        val port: Int
        if (cfg.disableConsul) {
            host = cfg.shardManagerHost
            port = cfg.shardManagerPort
        } else {
            val services = ConsulRegistry(cfg.consulHost, cfg.consulPort).getService(cfg.shardManagerServiceName)
            val service = if (services.response.size > 1) {
                services.response[ThreadLocalRandom.current().nextInt(0, services.response.size)]
            } else {
                services.response[0]
            }
            host = service.serviceAddress
            port = service.servicePort
        }
        log.info("Connecting to ShardManager instance on {}:{}", host, port)
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
    }
}