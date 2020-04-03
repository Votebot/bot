package space.votebot.bot.metrics

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.ticker
import mu.KotlinLogging
import net.dv8tion.jda.api.sharding.ShardManager
import space.votebot.bot.util.DefaultThreadFactory
import space.votebot.bot.util.InfluxDBConnection
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * GuildCountMetrics posts the number of Guilds to InfluxDB.
 * @param influx the [InfluxDBConnection]
 */
class GuildCountMetrics(private val shardManager: ShardManager, private val influx: InfluxDBConnection) {

    private val log = KotlinLogging.logger { }

    private val context = DefaultThreadFactory.newSingleThreadExecutor("guild-count-metrics").asCoroutineDispatcher()

    @OptIn(ObsoleteCoroutinesApi::class)
    private val scheduler = ticker(TimeUnit.SECONDS.toMillis(5), 0, context = context)

    /**
     * Starts posting stats
     */
    suspend fun start() {
        for (unit in scheduler) {
            influx.writePoint(Point.measurement("guilds").apply {
                addField("count", shardManager.guilds.size)
                time(Instant.now().toEpochMilli(), WritePrecision.MS)
            })
            log.debug { "Posted GuildCount Metrics." }
        }
    }

    /**
     * Stops posting stats and closes all used resources.
     */
    fun stop() {
        scheduler.cancel()
        context.close()
    }
}