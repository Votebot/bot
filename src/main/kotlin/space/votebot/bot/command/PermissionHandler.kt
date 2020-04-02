package space.votebot.bot.command

import space.votebot.bot.command.permission.Permission
import net.dv8tion.jda.api.entities.Member

/**
 * Handler for command permissions.
 */
interface PermissionHandler {

    /**
     * Checks whether the [executor] covers the [permission] or not.
     */
    fun isCovered(
        permission: Permission,
        executor: Member
    ): Boolean
}
