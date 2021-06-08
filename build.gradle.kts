plugins {
    kotlin("jvm") version "1.5.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    application
}

group = "me.schlaubi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://schlaubi.jfrog.io/artifactory/envconf")
}

dependencies {

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
            freeCompilerArgs =
                listOf(
                    "-Xopt-in=dev.kord.common.annotation.KordPreview",
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xopt-in=kotlin.ExperimentalStdlibApi"
                )
        }
    }
}
