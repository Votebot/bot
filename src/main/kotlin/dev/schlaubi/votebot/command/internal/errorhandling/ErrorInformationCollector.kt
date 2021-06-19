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

import dev.schlaubi.votebot.command.context.Context
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.coroutines.CoroutineContext

object ErrorInformationCollector {

    /**
     * Collects error information and returns them in a formatted way.
     * @param context the current command [Context]
     * @param coroutineContext the current [CoroutineContext]
     * @param throwable the thrown [Throwable]
     * @param thread the current [Thread]
     * @see DebugErrorHandler
     * @see ProductionErrorHandler
     */
    suspend fun collectErrorInformation(
        context: Context,
        coroutineContext: CoroutineContext,
        throwable: Throwable,
        thread: Thread
    ): String = coroutineScope {
        val kord = context.kord
        val guild = async { context.guild.asGuild() }
        val executor = async { context.executor.asMember() }
        val selfMember = async { guild.await().getMember(kord.selfId) }
        val channel = async { context.interaction.channel.asChannel() }
        val command = context.interaction.command

        return@coroutineScope """
            Command: ${command.rootName}(${command.rootId})
            Command Arguments:
            ${command.options}
            
            Guild: ${guild.await().let { "${it.name}(${it.id})" }}
            Executor: @${executor.await().let { "${it.tag}(${it.id.value})" }}
            Permissions: ${selfMember.await().getPermissions().code}
            
            TextChannel: #${channel.await().let { "${it.name}(${it.id})" }}
            Channel Permissions: ${channel.await().getEffectivePermissions(selfMember.await().id).code}
            
            Timestamp: ${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())}
            Thread:$thread
            Coroutine: $coroutineContext
            Stacktrace:
            ${throwable.stackTraceToString()}
        """.trimIndent()
    }
}
