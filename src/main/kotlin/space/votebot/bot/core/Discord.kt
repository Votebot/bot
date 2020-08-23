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
import space.votebot.bot.events.ShardWatcher

private val restActionLogger = KotlinLogging.logger("RestActions")

/**
 * Discord related stuff.
 *
 * @property shardManager See [ShardManager]
 */
class Discord(discordToken: String, httpClient: OkHttpClient, eventManager: IEventManager, private val bot: VoteBot) {

    private val log = KotlinLogging.logger {}

    val shardManager: ShardManager = DefaultShardManagerBuilder.createDefault(discordToken)
            .setEventManagerProvider { eventManager }
            .setHttpClient(httpClient)
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setActivity(Activity.playing("Starting ..."))
            .addEventListeners(this, ShardWatcher(bot))
            .build()

    init {
        RestAction.setDefaultFailure {
            restActionLogger.error(it) { "An error occurred while executing restaction." }
        }
    }

    /**
     * Handles [event].
     */
    @Suppress("UNUSED_PARAMETER")
    @EventSubscriber
    fun whenReady(event: AllShardsReadyEvent) {
        log.info { "Bot initialized!" }
        bot.gameAnimator.start()
    }
}