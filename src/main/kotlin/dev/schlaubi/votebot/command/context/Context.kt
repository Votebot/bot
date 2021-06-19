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

package dev.schlaubi.votebot.command.context

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.entity.interaction.GuildInteraction
import dev.kord.core.entity.interaction.InteractionCommand
import dev.schlaubi.votebot.command.ExecutableCommand
import dev.schlaubi.votebot.command.context.response.Responder
import dev.schlaubi.votebot.core.VoteBot

/**
 * The context in which an command gets executed.
 *
 * @see Arguments
 * @see Responder
 */
interface Context : Arguments, Responder {
    /**
     * The [VoteBot] instance which triggered this command
     */
    val bot: VoteBot

    /**
     * The [Kord] instance which triggered this command.
     */
    val kord: Kord get() = bot.kord

    /**
     * The [ExecutableCommand] which was executed (refers to `this` in command classes)
     */
    val command: ExecutableCommand

    /**
     * The [GuildInteraction] which triggered this command.
     */
    val interaction: GuildInteraction

    /**
     * The [InteractionCommand] which got executed.
     */
    val slashCommand: InteractionCommand get() = interaction.command

    /**
     * The user who executed this command.
     */
    val executor: MemberBehavior get() = interaction.member

    /**
     * The member of the bot on [guild].
     */
    @OptIn(KordUnsafe::class, KordExperimental::class)
    val me: MemberBehavior get() = interaction.kord.unsafe.member(interaction.guildId, interaction.kord.selfId)

    /**
     * The [GuildBehavior] on which the command was executed
     */
    val guild: GuildBehavior get() = interaction.guild

    /**
     * User equivalent of [me].
     */
    @OptIn(KordUnsafe::class, KordExperimental::class)
    val selfUser: UserBehavior
        get() = interaction.kord.unsafe.user(interaction.kord.selfId)
}
