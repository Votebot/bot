package space.votebot.bot.command.permission

/**
 * Enum for command permissions.
 */
enum class Permission {
    /**
     * Anyone can execute the command.
     */
    ANY,

    /**
     * Only administrators can execute the command.
     */
    ADMIN,

    /**
     * Commands only executable by bot owners.
     */
    BOT_OWNER
}
