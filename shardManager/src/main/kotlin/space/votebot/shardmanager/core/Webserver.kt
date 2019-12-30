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

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class Webserver(shardManager: ShardManager, port: Int) {

    init {
        embeddedServer(Netty, port) {
            routing {
                get("/health") {
                    call.respond(if (shardManager.statusHealthy) HttpStatusCode.OK else HttpStatusCode.InternalServerError)
                }
                get("/ready") {
                    call.respond(if (shardManager.statusReady) HttpStatusCode.OK else HttpStatusCode.InternalServerError)
                }
            }
        }.start()
    }
}