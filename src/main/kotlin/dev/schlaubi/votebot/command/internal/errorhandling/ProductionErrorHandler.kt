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

package dev.schlaubi.votebot.command.internal.errorhandling

import dev.schlaubi.votebot.command.CommandErrorHandler
import dev.schlaubi.votebot.command.context.Context
import dev.schlaubi.votebot.command.context.response.followUp
import dev.schlaubi.votebot.config.Config
import dev.schlaubi.votebot.util.Embeds
import dev.schlaubi.votebot.util.whenUsingSentry
import io.sentry.Sentry
import io.sentry.SentryLevel
import kotlin.coroutines.CoroutineContext

object ProductionErrorHandler : CommandErrorHandler {

    private val errorResponse = Embeds.error(
        "Unexpected Error",
        mutableListOf(
            ":x: | **An unexpected error has occurred!**",
        ).also {
            if (Config.ENVIRONMENT.useSentry) {
                it.addAll(
                    listOf(
                        "",
                        "> Error information will be collected",
                        "> and reported to the Development Team!"
                    )
                )
            }
        }.joinToString("\n")
    )

    override suspend fun handleCommandError(
        context: Context,
        coroutineContext: CoroutineContext,
        throwable: Throwable
    ) {
        context.followUp(errorResponse)
        whenUsingSentry {
            val errorInformation = ErrorInformationCollector.collectErrorInformation(
                context,
                coroutineContext,
                throwable,
                Thread.currentThread()
            )
            Sentry.captureMessage(errorInformation, SentryLevel.ERROR)
        }
    }
}
