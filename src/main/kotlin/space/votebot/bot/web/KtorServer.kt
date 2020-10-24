package space.votebot.bot.web

import io.ktor.application.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import space.votebot.bot.metrics.Metrics

class KtorServer(port: Int) {

    init {
        embeddedServer(Netty, port) {
            install(MicrometerMetrics) {
                registry = Metrics
                meterBinders = listOf(
                        ClassLoaderMetrics(),
                        JvmMemoryMetrics(),
                        JvmGcMetrics(),
                        ProcessorMetrics(),
                        JvmThreadMetrics(),
                )
            }

            routing {
                get("/") {
                    call.respond("VoteBot")
                }

                get("/metrics/prometheus") {
                    call.respond(Metrics.scrape())
                }
            }
        }.start(false)
    }
}