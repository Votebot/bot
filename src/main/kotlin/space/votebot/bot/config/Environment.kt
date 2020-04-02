package space.votebot.bot.config

/**
 * Environment types of VoteBot.
 */
enum class Environment(val debug: Boolean) {
    /**
     * Development environment.
     */
    DEVELOPMENT(true),

    /**
     * Staging environment.
     */
    STAGING(false),

    /**
     * Production environment
     */
    PRODUCTION(false);
}