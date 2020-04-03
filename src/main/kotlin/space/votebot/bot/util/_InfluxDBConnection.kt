package space.votebot.bot.util

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import java.time.Instant

interface InfluxDBConnection {
    fun writePoint(point: Point)

    fun writePointNow(point: Point)
}

/**
 * Wrapper for an [InfluxDBConnection] that enables us easy to write points to bucket of an org.
 */
class DefaultInfluxDBConnection(private val client: InfluxDBClient, private val bucket: String, private val org: String) : InfluxDBConnection {
    override fun writePoint(point: Point) {
        client.writeApi.writePoint(bucket, org, point)
    }

    override fun writePointNow(point: Point) {
        writePoint(point.time(Instant.now().toEpochMilli(), WritePrecision.MS))
    }
}

/**
 * No-op [InfluxDBConnection].
 */
class NopInfluxDBConnection : InfluxDBConnection {
    override fun writePoint(point: Point) {} // Do nothing
    override fun writePointNow(point: Point) {} // Do nothing
}