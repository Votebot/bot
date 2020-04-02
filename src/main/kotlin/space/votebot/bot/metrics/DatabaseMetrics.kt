package space.votebot.bot.metrics

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.ticker
import mu.KotlinLogging
import space.votebot.bot.util.DefaultThreadFactory
import java.net.InetAddress
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * DatabaseMetrics posts information about the the database connection pool to InfluxDB.
 * @param client the [InfluxDBClient]
 * @param bucket the InfluxDB bucket to post stats to
 * @param org the InfluxDB org to post stats to
 */
class DatabaseMetrics(private val dataSource: HikariDataSource, private val client: InfluxDBClient, private val bucket: String, private val org: String) {

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
            client.writeApi.writePoint(bucket, org, Point.measurement("db_connections").apply {
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
}