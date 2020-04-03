package space.votebot.bot.core

import com.influxdb.client.InfluxDBClientFactory
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.dv8tion.jda.api.hooks.IEventManager
import okhttp3.OkHttpClient
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.command.CommandClient
import space.votebot.bot.command.impl.CommandClientImpl
import space.votebot.bot.command.impl.DebugCommandHandler
import space.votebot.bot.command.impl.ProductionCommandHandler
import space.votebot.bot.commands.general.HelpCommand
import space.votebot.bot.config.Config
import space.votebot.bot.constants.Constants
import space.votebot.bot.database.VoteBotGuilds
import space.votebot.bot.event.AnnotatedEventManager
import space.votebot.bot.metrics.DatabaseMetrics
import space.votebot.bot.metrics.GuildCountMetrics
import space.votebot.bot.metrics.MemoryMetrics
import space.votebot.bot.util.DefaultInfluxDBConnection
import space.votebot.bot.util.InfluxDBConnection
import space.votebot.bot.util.NopInfluxDBConnection

class VoteBotImpl(private val config: Config) : VoteBot {

    private val log = KotlinLogging.logger { }
    private val dataSource: HikariDataSource
    override val influx: InfluxDBConnection
    override val eventManager: IEventManager = AnnotatedEventManager()
    override val httpClient: OkHttpClient = OkHttpClient()
    override val discord: Discord
    override val debugMode = config.environment.debug
    override val commandClient: CommandClient = CommandClientImpl(this, Constants.prefix)

    init {
        dataSource = initDatabase()
        discord = Discord(config.discordToken, httpClient, eventManager)

        influx = if (config.environment.debug && config.enableMetrics || config.environment.debug && !config.enableMetrics) {
            DefaultInfluxDBConnection(InfluxDBClientFactory.create(config.influxDbAddress, config.influxDbToken.toCharArray()), config.influxDbBucket, config.influxDbOrg)
        } else NopInfluxDBConnection()

        eventManager.register(commandClient)
        if (debugMode) {
            eventManager.register(DebugCommandHandler())
        } else {
            eventManager.register(ProductionCommandHandler(config.errorReportChannel))
        }
        registerCommands()
    }

    private fun initDatabase(): HikariDataSource {
        val dataSource = HikariDataSource().apply {
            jdbcUrl = config.dbAddress
            username = config.dbUser
            password = config.dbPassword
            driverClassName = "org.postgresql.Driver"
        }
        Database.connect(dataSource)
        transaction {
            SchemaUtils.createMissingTablesAndColumns(VoteBotGuilds)
        }
        return dataSource
    }

    private suspend fun initMetrics() {
        coroutineScope {
            // If metrics are disabled we usually just pass the no-op InfluxDBConnection. But as these are constantly
            // running we do this double check here.
            if (config.environment.debug && config.enableMetrics || config.environment.debug && !config.enableMetrics) {
                launch { MemoryMetrics(influx).start() }
                launch { DatabaseMetrics(dataSource, influx).start() }
                launch { GuildCountMetrics(discord.shardManager, influx).start() }
            }
        }
    }

    private fun registerCommands() {
        commandClient.registerCommands(HelpCommand())
    }

    suspend fun start() {
        initMetrics()
    }
}
