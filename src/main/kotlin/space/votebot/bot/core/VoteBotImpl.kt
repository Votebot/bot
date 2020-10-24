package space.votebot.bot.core

import com.zaxxer.hikari.HikariDataSource
import net.dv8tion.jda.api.hooks.IEventManager
import okhttp3.OkHttpClient
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.command.CommandClient
import space.votebot.bot.command.impl.CommandClientImpl
import space.votebot.bot.command.impl.DebugCommandHandler
import space.votebot.bot.command.impl.ProductionCommandHandler
import space.votebot.bot.command.permission.PermissionNodes
import space.votebot.bot.commands.general.HelpCommand
import space.votebot.bot.commands.owner.StatusCommand
import space.votebot.bot.commands.owner.TestCommand
import space.votebot.bot.commands.settings.LanguageCommand
import space.votebot.bot.commands.settings.PermissionsCommand
import space.votebot.bot.commands.settings.PrefixCommand
import space.votebot.bot.config.Config
import space.votebot.bot.data.VoteBotGuilds
import space.votebot.bot.data.VoteBotUsers
import space.votebot.bot.event.AnnotatedEventManager
import space.votebot.bot.metrics.DatabaseMetrics
import space.votebot.bot.metrics.GuildCountMetrics
import space.votebot.bot.web.KtorServer

internal class VoteBotImpl(private val config: Config) : VoteBot {

    private val dataSource: HikariDataSource
    override val eventManager: IEventManager = AnnotatedEventManager()
    override val httpClient: OkHttpClient = OkHttpClient()
    override val discord: Discord
    override val debugMode = config.environment.debug
    override val gameAnimator: GameAnimator
    override val commandClient: CommandClient = CommandClientImpl(this, Config.defaultPrefix)

    init {
        KtorServer(config.httpPort)
        dataSource = initDatabase()
        DatabaseMetrics(dataSource)
        discord = Discord(config.discordToken, httpClient, eventManager, this)
        GuildCountMetrics(discord.shardManager)
        gameAnimator = GameAnimator(discord, config)

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
            SchemaUtils.createMissingTablesAndColumns(VoteBotGuilds, VoteBotUsers, PermissionNodes)
        }
        return dataSource
    }

    private fun registerCommands() {
        commandClient.registerCommands(
                HelpCommand(),
                PrefixCommand(),
                StatusCommand(),
                TestCommand(),
                LanguageCommand(),
                PermissionsCommand()
        )
    }
}
