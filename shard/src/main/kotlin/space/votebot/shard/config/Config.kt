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

package space.votebot.shard.config

import io.github.cdimascio.dotenv.dotenv

class Config {

    private val dotenv = dotenv {
        ignoreIfMissing = true
    }

    val sentryDsn
        get() = dotenv["${PREFIX}SENTRY_DSN"]!!

    val disableConsul
        get() = dotenv["${PREFIX}DISABLE_CONSUL"]?.toBoolean() ?: false

    val consulHost
        get() = dotenv["${PREFIX}CONSUL_HOST"] ?: "localhost"

    val consulPort
        get() = dotenv["${PREFIX}CONSUL_PORT"]?.toInt() ?: 8500

    val shardManagerServiceName
        get() = dotenv["${PREFIX}SHARDMANAGER_SERVICE_NAME"] ?: "shard-service-votebot"

    val shardManagerHost
        get() = dotenv["${PREFIX}SHARDMANAGER_HOST"] ?: "localhost"

    val shardManagerPort
        get() = dotenv["${PREFIX}SHARDMANAGER_PORT"]?.toInt() ?: 5465


    companion object {
        const val PREFIX = "SHARD_"
    }
}