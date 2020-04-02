package space.votebot.bot.metrics

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.ticker
import mu.KotlinLogging
import net.dv8tion.jda.api.sharding.ShardManager
import space.votebot.bot.util.DefaultThreadFactory
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * GuildCountMetrics posts the number of Guilds to InfluxDB.
 * @param client the [InfluxDBClient]
 * @param bucket the InfluxDB bucket to post stats to
 * @param org the InfluxDB org to post stats to
 */
class GuildCountMetrics(private val shardManager: ShardManager, private val client: InfluxDBClient, private val bucket: String, private val org: String) {

    private val log = KotlinLogging.logger { }

    private val context = DefaultThreadFactory.newSingleThreadExecutor("guild-count-metrics").asCoroutineDispatcher()

    @OptIn(ObsoleteCoroutinesApi::class)
    private val scheduler = ticker(TimeUnit.SECONDS.toMillis(5), 0, context = context)

    /**
     * Starts posting stats
     */
    suspend fun start() {
        for (unit in scheduler) {
            client.writeApi.writePoint(bucket, org, Point.measurement("guilds").apply {
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