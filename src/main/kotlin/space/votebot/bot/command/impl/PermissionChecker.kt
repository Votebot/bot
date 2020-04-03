package space.votebot.bot.command.impl

import net.dv8tion.jda.api.entities.Member
import space.votebot.bot.command.PermissionHandler
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.constants.Constants

class PermissionChecker : PermissionHandler {
    override fun isCovered(permission: Permission, executor: Member): Boolean {
        if (permission == Permission.ANY) return true
        if (executor.idLong in Constants.BOT_OWNERS) return true
        if (permission == Permission.ADMIN) {
            executor.roles.any { it.hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR) }
        }
        return isPrivileged(executor)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun isPrivileged(executor: Member): Boolean {
        return false //TODO: Implement privileged system.
    }
}