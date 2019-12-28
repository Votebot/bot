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

plugins {
    java
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                "Main-Class" to "space.votebot.shard.ApplicationKt"
        ))
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":common"))
    implementation(project(":shardManagerAPI"))
}