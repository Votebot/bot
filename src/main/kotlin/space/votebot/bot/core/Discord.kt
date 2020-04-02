package space.votebot.bot.core

import mu.KotlinLogging
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import okhttp3.OkHttpClient

private val restActionLogger = KotlinLogging.logger("RestActions")

class Discord(discordToken: String, httpClient: OkHttpClient, eventManager: IEventManager) {

    val shardManager: ShardManager = DefaultShardManagerBuilder.createDefault(discordToken)
            .setEventManagerProvider { eventManager }
            .setHttpClient(httpClient)
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setActivity(Activity.playing("Staring ..."))
            .build()

    init {
        RestAction.setDefaultFailure {
            restActionLogger.error(it) { "An error occurred while executing restaction" }
        }
    }
}