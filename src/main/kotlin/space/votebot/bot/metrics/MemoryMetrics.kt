package space.votebot.bot.metrics

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.ticker
import mu.KotlinLogging
import java.net.InetAddress
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * MemoryMetrics posts the amount of total heap and the amount of allocated heap every second to InfluxDB.
 * @param client the [InfluxDBClient]
 * @param bucket the InfluxDB bucket to post stats to
 * @param org the InfluxDB org to post stats to
 */
class MemoryMetrics(private val client: InfluxDBClient, private val bucket: String, private val org: String) {

    private val log = KotlinLogging.logger { }

    private val context = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    @OptIn(ObsoleteCoroutinesApi::class)
    private val scheduler = ticker(TimeUnit.SECONDS.toMillis(5), 0, context = context)

    private val hostName = InetAddress.getLocalHost().hostName

    /**
     * Starts posting stats
     */
    suspend fun start() {
        for (unit in scheduler) {
            client.writeApi.writePoint(bucket, org, Point.measurement("memory").apply {
                addTag("host", hostName)
                addTag("java.version", System.getProperty("java.version"))
                addTag("java.vm.name", System.getProperty("java.vm.name"))
                addField("total", Runtime.getRuntime().totalMemory())
                addField("allocated", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
                time(Instant.now().toEpochMilli(), WritePrecision.MS)
            })
            log.debug { "Posted Memory Metrics." }
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