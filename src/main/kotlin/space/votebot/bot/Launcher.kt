package space.votebot.bot

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import io.sentry.Sentry
import org.slf4j.LoggerFactory
import space.votebot.bot.config.Config
import space.votebot.bot.config.Environment
import space.votebot.bot.core.VoteBot

fun main() {
    val cfg = Config()
    val logLevel = Level.valueOf(cfg.logLevel)

    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    rootLogger.level = logLevel

    if (cfg.environment != Environment.DEVELOPMENT) {
        Sentry.init(cfg.sentryDSN)
    } else {
        Sentry.init()
    }

    val bot = VoteBot(cfg)
    bot.start()
}