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

package space.votebot.shardmanager.config

import io.github.cdimascio.dotenv.dotenv

class Config {

    private val dotenv = dotenv {
        ignoreIfMissing = true
    }

    fun consulHost() = dotenv["${PREFIX}CONSUL_HOST"] ?: "localhost"
    fun consulPort() = Integer.parseInt(dotenv["${PREFIX}CONSUL_PORT"] ?: "8500")
    fun sentryDsn() = dotenv["${PREFIX}SENTRY_DSN"]!!

    companion object {
        const val PREFIX = "SHARDMANAGER_"
    }
}