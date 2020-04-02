package space.votebot.bot.core

import net.dv8tion.jda.api.hooks.IEventManager
import okhttp3.OkHttpClient
import space.votebot.bot.config.Config
import space.votebot.bot.event.AnnotatedEventManager

class VoteBot(private val config: Config) {

    val eventManager: IEventManager = AnnotatedEventManager()
    val httpClient: OkHttpClient = OkHttpClient()

    val discord: Discord = Discord(config.discordToken, httpClient, eventManager)


}