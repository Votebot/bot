package space.votebot.bot.events

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.sharding.ShardManager
import space.votebot.bot.core.VoteBot
import space.votebot.bot.event.EventDescriber
import space.votebot.bot.event.EventSubscriber
import javax.annotation.Nonnull

/**
 * Event that is fired when every shard of this instance is ready.
 */
@EventDescriber(callParents = false)
class AllShardsReadyEvent(api: @Nonnull JDA, responseNumber: Long, val shardManager: ShardManager) : ReadyEvent(api, responseNumber)

/**
 * Watches shard for beeing ready.
 */
class ShardWatcher(private val bot: VoteBot) {

    /**
     * Listen for shard READY events.
     */
    @EventSubscriber
    fun onShardReady(event: ReadyEvent) {
        val shardManager = bot.discord.shardManager
        if (shardManager.shardsTotal == shardManager.shardsRunning) {
            bot.eventManager.handle(AllShardsReadyEvent(event.jda, event.responseNumber, shardManager))
        }
    }
}
