/*
 * VoteBot
 * Copyright (C) 2019 VoteBot
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package space.votebot.common

import com.google.common.net.HostAndPort
import com.orbitz.consul.Consul
import com.orbitz.consul.model.agent.ImmutableRegistration
import com.orbitz.consul.model.agent.Registration
import com.orbitz.consul.option.QueryOptions
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ConsulRegistry(private val serviceId: String, consulHost: String, consulPort: Int) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val client = Consul.builder().withHostAndPort(HostAndPort.fromHost(consulHost).withDefaultPort(consulPort)).build()

    init {
        Runtime.getRuntime().addShutdownHook(Thread(this::unregister))
    }

    fun register(port: Int) {
        log.debug("Registering service in service registry...")
        client.agentClient().register(ImmutableRegistration.builder()
                .id(serviceId)
                .name(serviceId)
                .port(port)
                .addChecks(Registration.RegCheck.ttl(10))
                .build())
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::updateStatus, 0, 5, TimeUnit.SECONDS)
        log.info("Registered service in service registry.")
    }

    fun getService(id: String) = client.agentClient().getService(id, QueryOptions.BLANK).response

    private fun updateStatus() {
        log.debug("Updating service status in service registry...")
        client.agentClient().pass(serviceId)
        log.debug("Updated service status in service registry.")
    }

    private fun unregister() {
        log.debug("Deregistering service in service registry...")
        client.agentClient().deregister(serviceId)
        log.info("Deregistered service in service registry.")
    }
}