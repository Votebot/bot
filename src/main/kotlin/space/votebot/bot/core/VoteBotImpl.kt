package space.votebot.bot.core

import com.influxdb.client.InfluxDBClientFactory
import mu.KotlinLogging
import net.dv8tion.jda.api.hooks.IEventManager
import okhttp3.OkHttpClient
import space.votebot.bot.command.CommandClient
import space.votebot.bot.command.impl.CommandClientImpl
import space.votebot.bot.command.impl.DebugCommandHandler
import space.votebot.bot.command.impl.ProductionCommandHandler
import space.votebot.bot.commands.general.HelpCommand
import space.votebot.bot.config.Config
import space.votebot.bot.constants.Constants
import space.votebot.bot.event.AnnotatedEventManager
import space.votebot.bot.metrics.MemoryMetrics

class VoteBotImpl(config: Config) : VoteBot {

    private val log = KotlinLogging.logger { }
    private var memoryMetrics: MemoryMetrics? = null
    override val eventManager: IEventManager = AnnotatedEventManager()
    override val httpClient: OkHttpClient = OkHttpClient()
    override val discord: Discord = Discord(config.discordToken, httpClient, eventManager)
    override val debugMode = config.environment.debug
    override val commandClient: CommandClient = CommandClientImpl(this, Constants.prefix)

    init {
        if (config.environment.debug && config.enableMetrics || config.environment.debug && !config.enableMetrics) {
            log.info { "Enabled metrics." }
            val influxDBClient = InfluxDBClientFactory.create(config.influxDbAddress, config.influxDbToken.toCharArray())
            memoryMetrics = MemoryMetrics(influxDBClient, config.influxDbBucket, config.influxDbOrg)
        }

        eventManager.register(commandClient)
        if (debugMode) {
            eventManager.register(DebugCommandHandler())
        } else {
            eventManager.register(ProductionCommandHandler(config.errorReportChannel))
        }
        registerCommands()
    }

    private fun registerCommands() {
        commandClient.registerCommands(HelpCommand())
    }

    suspend fun start() {
        memoryMetrics?.start()
    }
}
