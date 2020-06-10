package space.votebot.bot.util

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel

/**
 * Checks whether the bot itself has [permission].
 */
fun Guild.iHavePermission(vararg permission: Permission) = selfMember.hasPermission(*permission)

/**
 * Checks whether the bot itself has [permission] in this [GuildChannel].
 */
fun GuildChannel.iHavePermission(vararg permission: Permission) = guild.selfMember.hasPermission(this, *permission)

/**
 * Checks whether the bot itself has [Permission.MESSAGE_MANAGE] in this [GuildChannel].
 */
val GuildChannel.reactionsReady: Boolean
    get() = iHavePermission(Permission.MESSAGE_MANAGE)