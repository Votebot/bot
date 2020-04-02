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
     * Bot owner exclusive commands.
     */
    BOT_OWNER("Bot Devloper"),

    /**
     * Voting related commands.
     */
    VOTING("Voting")
}
