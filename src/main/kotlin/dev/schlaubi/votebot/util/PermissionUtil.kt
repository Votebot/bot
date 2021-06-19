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

package dev.schlaubi.votebot.util

import dev.kord.common.entity.DiscordGuildApplicationCommandPermission
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.ApplicationCommandPermissionsModifyBuilder
import dev.kord.rest.request.KtorRequestException
import kotlinx.coroutines.flow.toList
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Modifies the permissions of [id] without overwriting existing permissions.
 *
 * @param kord the [Kord] instance to send the request
 * @param guildId the id of the guild to update the permissions on
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun appendPermission(
    id: Snowflake,
    kord: Kord,
    guildId: Snowflake,
    crossinline builder: ApplicationCommandPermissionsModifyBuilder.() -> Unit
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val permissions = try {
        kord.slashCommands.getApplicationCommandPermissions(kord.slashCommands.applicationId, guildId, id).permissions.toList()
    } catch (e: KtorRequestException) {
        emptyList() // Discord will error if the command doesn't have permissions yet
    }

    kord.slashCommands.editApplicationCommandPermissions(kord.slashCommands.applicationId, guildId, id) {
        this.permissions = permissions.map {
            DiscordGuildApplicationCommandPermission(
                it.id,
                it.type,
                it.permission
            )
        }.toMutableList()

        builder(this)
    }
}
