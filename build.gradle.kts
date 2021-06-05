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
    implementation("io.github.microutils", "kotlin-logging", "2.0.8")

    implementation("dev.kord", "kord-core", "0.7.x-SNAPSHOT")

    implementation("dev.schlaubi", "envconf", "1.0")

    implementation("ch.qos.logback", "logback-classic", "1.3.0-alpha5")
}
