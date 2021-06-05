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

package dev.schlaubi.votebot

import ch.qos.logback.classic.Logger
import dev.schlaubi.votebot.config.Config
import dev.schlaubi.votebot.config.Environment
import io.sentry.Sentry
import io.sentry.SentryOptions
import org.slf4j.LoggerFactory

suspend fun main() {
    initializeLogging()
    initializeSentry()
}

private fun initializeLogging() {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    rootLogger.level = Config.LOG_LEVEL
}

private fun initializeSentry() {
    val configure: (SentryOptions) -> Unit =
        if (Config.ENVIRONMENT != Environment.DEVELOPMENT) {
            { it.dsn = Config.SENTRY_TOKEN; }
        } else {
            { it.dsn = "" }
        }

    Sentry.init(configure)
}
