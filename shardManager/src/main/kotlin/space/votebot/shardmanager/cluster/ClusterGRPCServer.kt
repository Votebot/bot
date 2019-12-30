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

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import mu.KotlinLogging
import space.votebot.shardmanager.api.cluster.ConnectRequest
import space.votebot.shardmanager.api.cluster.ConnectResponse
import space.votebot.shardmanager.api.cluster.ShardManagerClusterGrpc
import space.votebot.shardmanager.core.ShardManager

class ClusterGRPCServer(port: Int, shardManager: ShardManager) {

    private val log = KotlinLogging.logger {}
    private val server: Server

    init {
        server = ServerBuilder.forPort(port)
                .addService(Handler(shardManager))
                .build()
    }

    fun run() {
        log.trace { "Starting cluster gRPC server..." }
        server.start()
        log.info { "Started cluster gRPC server on port." }
    }

    class Handler(private val shardManager: ShardManager) : ShardManagerClusterGrpc.ShardManagerClusterImplBase() {
        private val log = KotlinLogging.logger {}

        override fun connect(request: ConnectRequest, responseObserver: StreamObserver<ConnectResponse>) {
            log.info { "Connection request from: ${request.address}:${request.port}." }
            if (shardManager.shardManagerRegistry.registerShardManager(ClusterShardManager(request.address, request.port)))
                log.info { "ShardManager registered: ${request.address}:${request.port}." }
            else
                log.info { "ShardManager already registered: ${request.address}:${request.port}." }
            log.debug { "Synchronizing shards to: ${request.address}:${request.port}..." }
            responseObserver.onNext(ConnectResponse.newBuilder()
                    .addAllShards(shardManager.shardRegistry.shards.map { it.toProtoShard() })
                    .build())
            responseObserver.onCompleted()
            log.debug { "Synchronized shards to: ${request.address}:${request.port}." }
        }
    }
}