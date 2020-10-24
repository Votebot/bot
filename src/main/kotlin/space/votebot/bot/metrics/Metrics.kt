package space.votebot.bot.metrics

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

object Metrics : PrometheusMeterRegistry(PrometheusConfig.DEFAULT)