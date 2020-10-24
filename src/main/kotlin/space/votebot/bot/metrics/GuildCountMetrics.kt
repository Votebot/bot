package space.votebot.bot.metrics

import io.micrometer.core.instrument.Gauge
import net.dv8tion.jda.api.sharding.ShardManager

/**
 * GuildCountMetrics collects the guild count.
 */
class GuildCountMetrics(private val shardManager: ShardManager) {
    init {
        Gauge.builder("guild_count") {
            shardManager.guilds.size
        }.register(Metrics)
    }
}