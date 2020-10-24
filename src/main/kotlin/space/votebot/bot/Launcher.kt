package space.votebot.bot

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import io.sentry.Sentry
import org.slf4j.LoggerFactory
import space.votebot.bot.config.Config
import space.votebot.bot.config.Environment
import space.votebot.bot.core.VoteBotImpl as VoteBot

suspend fun main() {
    val logLevel = Level.valueOf(Config.logLevel)

    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    rootLogger.level = logLevel

    if (Config.environment != Environment.DEVELOPMENT) {
        Sentry.init {
            it.dsn = Config.sentryDSN
            it.environment = Config.environment.name
        }
    } else {
        Sentry.init()
    }

    VoteBot(Config)
}
