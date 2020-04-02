import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
    kotlin("jvm") version "1.3.70"
}

group = "space.votebot"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/ktor")
}

dependencies {
    // Logging
    implementation("org.slf4j", "slf4j-api", "2.0.0-alpha1")
    implementation("ch.qos.logback", "logback-classic", "1.3.0-alpha5")
    implementation("io.github.microutils", "kotlin-logging", "1.7.9")

    // Sentry
    implementation("io.sentry", "sentry", "1.7.30")
    implementation("io.sentry", "sentry-logback", "1.7.30")

    // Metrics
    implementation("com.influxdb", "influxdb-client-java", "1.6.0")

    // Ktor
    implementation("io.ktor", "ktor-server-netty", "1.3.2")
    implementation("io.ktor", "ktor-server-core", "1.3.2")
    implementation("io.ktor", "ktor-websockets", "1.3.2")
    implementation("io.ktor", "ktor-jackson", "1.3.2")
    testImplementation("io.ktor", "ktor-server-tests", "1.3.2")

    // JDA
    implementation("net.dv8tion", "JDA", "4.1.1_127")

    // Database
    implementation("org.jetbrains.exposed", "exposed-core", "0.22.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.22.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.22.1")
    implementation("org.jetbrains.exposed", "exposed-java-time", "0.22.1")

    // Util
    implementation("io.github.cdimascio", "java-dotenv", "5.1.4")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))

    // Tests
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit5", "1.3.71")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.1")
}

application {
    mainClassName = "space.votebot.bot.LauncherKt"
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

    "shadowJar"(ShadowJar::class) {
        archiveFileName.set("bot.jar")
    }
}