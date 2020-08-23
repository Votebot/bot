/*package space.votebot.bot.metrics

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import mu.KotlinLogging
import space.votebot.bot.util.InfluxDBConnection
import java.net.InetAddress
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * MemoryMetrics posts the number of total heap and the amount of allocated heap to InfluxDB.
 * @param influx the [InfluxDBConnection]
 */
class MemoryMetrics(private val influx: InfluxDBConnection) {

    private val log = KotlinLogging.logger { }

    @OptIn(ObsoleteCoroutinesApi::class)
    private val scheduler = ticker(TimeUnit.SECONDS.toMillis(5), 0)

    private val hostName = InetAddress.getLocalHost().hostName

    /**
     * Starts posting stats
     */
    suspend fun start() {
        for (unit in scheduler) {
            influx.writePoint(Point.measurement("memory").apply {
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
    }
}*/