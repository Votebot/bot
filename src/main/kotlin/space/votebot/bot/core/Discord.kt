package space.votebot.bot.core

import mu.KotlinLogging
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import okhttp3.OkHttpClient
import space.votebot.bot.event.EventSubscriber
import space.votebot.bot.events.AllShardsReadyEvent

private val restActionLogger = KotlinLogging.logger("RestActions")

class Discord(discordToken: String, httpClient: OkHttpClient, eventManager: IEventManager) {

    val log = KotlinLogging.logger {}

    val shardManager: ShardManager = DefaultShardManagerBuilder.createDefault(discordToken)
            .setEventManagerProvider { eventManager }
            .setHttpClient(httpClient)
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setActivity(Activity.playing("Starting ..."))
            .addEventListeners(this)
            .build()

    init {
        RestAction.setDefaultFailure {
            restActionLogger.error(it) { "An error occurred while executing restaction." }
        }
    }

    @EventSubscriber
    fun whenReady(event: AllShardsReadyEvent) {
        log.info { "Bot initialized!" }
    }
}