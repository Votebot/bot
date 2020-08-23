package space.votebot.bot.command

import net.dv8tion.jda.api.entities.Member

/**
 * Handler for command permissions.
 */
interface PermissionHandler {

    /**
     * Checks whether the [executor] covers the [command] or not.
     */
    fun isCovered(
            command: AbstractCommand,
            executor: Member
    ): Boolean
}
