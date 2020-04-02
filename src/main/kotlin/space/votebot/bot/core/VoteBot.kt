package space.votebot.bot.core

import net.dv8tion.jda.api.hooks.IEventManager
import okhttp3.OkHttpClient
import space.votebot.bot.command.CommandClient

interface VoteBot {
    val eventManager: IEventManager
    val httpClient: OkHttpClient
    val discord: Discord
    val debugMode: Boolean
    val commandClient: CommandClient
}