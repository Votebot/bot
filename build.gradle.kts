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
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    application
}

allprojects {
    group = "dev.schlaubi.votebot"
    version = "1.0-SNAPSHOT"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://schlaubi.jfrog.io/artifactory/envconf")
}


subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    val extension = (extensions.findByName("kotlin") as? org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension)

    extension?.apply {
        explicitApi()

        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "16"
                }
            }
        }

        js(BOTH) {
            browser()
            nodejs()
        }
    }
}

dependencies {
    implementation(client())
    runtimeOnly(logbackConfig())

    implementation("dev.kord", "kord-core", "0.7.x-SNAPSHOT")

    implementation("dev.schlaubi", "envconf", "1.0")

    implementation("io.github.microutils", "kotlin-logging", "2.0.8")
    implementation("ch.qos.logback", "logback-classic", "1.3.0-alpha5")
    implementation("io.sentry", "sentry", "4.3.0")
    implementation("io.sentry", "sentry-logback", "4.3.0")

}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = Jvm.target
            freeCompilerArgs =
                listOf(
                    "-Xopt-in=dev.kord.common.annotation.KordPreview",
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xopt-in=kotlin.ExperimentalStdlibApi"
                )
        }
    }
}
