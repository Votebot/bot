package space.votebot.bot.core

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

class VoteBotImpl(config: Config) : VoteBot {

    override val eventManager: IEventManager = AnnotatedEventManager()
    override val httpClient: OkHttpClient = OkHttpClient()

    override val discord: Discord = Discord(config.discordToken, httpClient, eventManager)

    override val debugMode = config.environment.debug

    override val commandClient: CommandClient = CommandClientImpl(this, Constants.prefix)

    init {
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

}
