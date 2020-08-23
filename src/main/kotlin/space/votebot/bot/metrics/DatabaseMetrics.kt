/*package space.votebot.bot.metrics

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.ticker
import mu.KotlinLogging
import space.votebot.bot.util.DefaultThreadFactory
import space.votebot.bot.util.InfluxDBConnection
import java.net.InetAddress
import java.time.Instant
import java.util.concurrent.TimeUnit


/**
 * DatabaseMetrics posts information about the the database connection pool to InfluxDB.
 * @param influx the [InfluxDBConnection]
 */
class DatabaseMetrics(private val dataSource: HikariDataSource, private val influx: InfluxDBConnection) {

    private val log = KotlinLogging.logger { }

    private val context = DefaultThreadFactory.newSingleThreadExecutor("db-metrics").asCoroutineDispatcher()

    @OptIn(ObsoleteCoroutinesApi::class)
    private val scheduler = ticker(TimeUnit.SECONDS.toMillis(5), 0, context = context)

    private val hostName = InetAddress.getLocalHost().hostName

    /**
     * Starts posting stats
     */
    suspend fun start() {
        for (unit in scheduler) {
            influx.writePoint(Point.measurement("db_connections").apply {
                addTag("host", hostName)
                addField("active", dataSource.hikariPoolMXBean.activeConnections)
                addField("idle", dataSource.hikariPoolMXBean.idleConnections)
                addField("total", dataSource.hikariPoolMXBean.totalConnections)
                time(Instant.now().toEpochMilli(), WritePrecision.MS)
            })
            log.debug { "Posted UserCount Metrics." }
        }
    }

    /**
     * Stops posting stats and closes all used resources.
     */
    fun stop() {
        scheduler.cancel()
        context.close()
    }
}*/