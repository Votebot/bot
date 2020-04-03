package space.votebot.bot.command

/**
 * Category of an [AbstractCommand].
 * @property displayName the name that is displayed in help messages
 */
enum class CommandCategory(val displayName: String) {
    /**
     * General commands.
     */
    GENERAL("General"),

    /**
     * Settings commands.
     */
    SETTINGS("Settings"),

    /**
     * Voting related commands.
     */
    VOTING("Voting"),

    /**
     * Bot owner exclusive commands.
     */
    BOT_OWNER("Bot Devloper")
}
