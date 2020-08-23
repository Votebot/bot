package space.votebot.bot.metrics

import com.zaxxer.hikari.HikariDataSource
import io.micrometer.core.instrument.Gauge

/**
 * DatabaseMetrics collects metrics about the database connections.
 */
class DatabaseMetrics(private val dataSource: HikariDataSource) {

    init {
        Gauge.builder("db_connections") {
            dataSource.hikariPoolMXBean.activeConnections
        }.tags("status", "active").register(Metrics)

        Gauge.builder("db_connections") {
            dataSource.hikariPoolMXBean.idleConnections
        }.tags("status", "idle").register(Metrics)

        Gauge.builder("db_connections") {
            dataSource.hikariPoolMXBean.totalConnections
        }.tags("status", "total").register(Metrics)
    }
}