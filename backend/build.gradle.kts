/*
 * VoteBot - A feature-rich bot to create votes on Discord guilds.
 *
 * Copyright (C) 2019-2021  Michael Rittmeister & Yannick Seeger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

application {
    mainClass.set("dev.schlaubi.votebot.LauncherKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(common())
    runtimeOnly(logbackConfig())

    implementation(ktorBom())
    implementation("io.ktor", "ktor-server-core")
    implementation("io.ktor", "ktor-auth")
    implementation("io.ktor", "ktor-auth-jwt")
    implementation("io.ktor", "ktor-locations")
    implementation("io.ktor", "ktor-client-core")
    implementation("io.ktor", "ktor-client-core-jvm")
    implementation("io.ktor", "ktor-client-apache")
    implementation("io.ktor", "ktor-server-host-common")
    implementation("io.ktor", "ktor-serialization")
    implementation("io.ktor", "ktor-websockets")
    implementation("io.ktor", "ktor-server-netty")

    implementation("org.litote.kmongo", "kmongo-coroutine-serialization", "4.2.8")

    implementation("ch.qos.logback", "logback-classic", "1.2.3")
}