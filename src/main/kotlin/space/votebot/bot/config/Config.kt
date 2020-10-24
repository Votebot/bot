package space.votebot.bot.config

import ch.qos.logback.classic.Level
import io.github.cdimascio.dotenv.dotenv

/**
 * Configuration for this service.
 */
object Config {

    private val dotenv = dotenv {
        ignoreIfMissing = true
    }


    private const val PREFIX = "BOT_"

    /**
     * The [Level] of the logger.
     */
    val logLevel: String = dotenv["${PREFIX}LOG_LEVEL"] ?: Level.INFO.levelStr

    /**
     * The sentry DSN.
     */
    val sentryDSN: String = dotenv["${PREFIX}SENTRY_DSN"] ?: ""

    /**
     * The [Environment] of the current instance.
     */
    val environment: Environment =
            Environment.valueOf(dotenv["${PREFIX}ENVIRONMENT"] ?: Environment.PRODUCTION.toString())

    val httpPort: Int = dotenv["${PREFIX}HTTP_PORT"]?.toInt() ?: 3500

    /**
     * The Discord Bot token.
     */
    val discordToken: String = dotenv["${PREFIX}DISCORD_TOKEN"] ?: ""

    /**
     * The id of the channel the error handler should send the messages to.
     */
    val errorReportChannel: Long = dotenv["${PREFIX}ERROR_REPORT_CHANNEL"]?.toLong() ?: 0

    /**
     * The database address.
     * Must be something like `jdbc:postgresql://host/database`.
     */
    val dbAddress: String = dotenv["${PREFIX}DB_ADDRESS"] ?: "jdbc:postgresql://localhost/postgres"

    /**
     * The database user.
     */
    val dbUser: String = dotenv["${PREFIX}DB_USER"] ?: "votebot"

    /**
     * The database password.
     */
    val dbPassword: String = dotenv["${PREFIX}DB_PASSWORD"] ?: "votebot"

    /**
     * A list of games for the Bot's status.
     */
    val rawGameAnimatorGames: List<String> = dotenv["${PREFIX}GAMES"]?.run { split(',') }
            ?: listOf("online:default:VoteBot", "online:listening:%users% users")

    /**
     * The default prefix
     */
    val defaultPrefix: String = dotenv["${PREFIX}DEFAULT_PREFIX"] ?: "v!"

}