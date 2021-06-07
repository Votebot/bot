/*
 * Votebot - A feature-rich bot to create votes on Discord guilds.
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

package dev.schlaubi.votebot.config

import ch.qos.logback.classic.Level
import dev.kord.common.entity.Snowflake
import dev.schlaubi.envconf.environment
import dev.schlaubi.envconf.getEnv
import dev.schlaubi.votebot.command.CommandErrorHandler
import dev.schlaubi.votebot.command.internal.errorhandling.DebugErrorHandler
import dev.schlaubi.votebot.command.internal.errorhandling.ProductionErrorHandler

/**
 * Environment based config.
 *
 * **Note:** All Properties are resolved by looking up the environment variable with the same name as the property
 */
object Config {

    /**
     * The Discord bot token.
     */
    val DISCORD_TOKEN by environment

    /**
     * The id of the dev guild if using [Environment.DEVELOPMENT].
     */
    val DEV_GUILD by getEnv { Snowflake(it) }.optional()

    /**
     * The Environment this instance runs in.
     *
     * @see Environment
     */
    val ENVIRONMENT by getEnv("", Environment.PRODUCTION, Environment::valueOf)

    /**
     * The LOG level of the root logger.
     */
    val LOG_LEVEL: Level by getEnv(default = Level.INFO) { Level.toLevel(it) }

    /**
     *  The Sentry DSN.
     */
    val SENTRY_TOKEN by getEnv().optional()
}

/**
 * Environmentally based settings.
 *
 * @property useGlobalCommands whether this should use only guild commands on [Config.DEV_GUILD] or global commands
 * @property useSentry whether sentry error logging is enabled or not
 * @property errorHandler the [CommandErrorHandler] used in this environment
 */
enum class Environment(
    val useGlobalCommands: Boolean = true,
    val useSentry: Boolean = true,
    val errorHandler: CommandErrorHandler
) {
    /**
     * Production environment:
     * - Global commands
     * - Sentry error handling
     */
    PRODUCTION(errorHandler = ProductionErrorHandler),

    /**
     * Development environment:
     * - no sentry
     * - guild commands on [Config.DEV_GUILD]
     */
    DEVELOPMENT(false, false, DebugErrorHandler)
}
