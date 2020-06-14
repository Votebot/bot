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
    maven("https://dl.bintray.com/votebot/maven" )
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
    implementation("net.dv8tion", "JDA", "4.1.1_131") {
        exclude(module = "opus-java")
    }

    // Database
    implementation("org.jetbrains.exposed", "exposed-core", "0.22.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.22.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.22.1")
    implementation("org.jetbrains.exposed", "exposed-java-time", "0.22.1")
    implementation("com.zaxxer", "HikariCP", "3.4.2")
    implementation("org.postgresql", "postgresql", "42.2.12")

    // Util
    implementation("io.github.cdimascio", "java-dotenv", "5.1.4")
    implementation("com.squareup.okhttp3", "okhttp", "4.4.0")
    implementation("xyz.downgoon", "snowflake", "1.0.0")

    // i18next
    implementation("com.i18next", "i18next-kein-android", "1.0")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.3.5")

    // Tests
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit5", "1.3.71")
    testImplementation("org.mockito", "mockito-core", "3.3.3")
    testImplementation("com.nhaarman.mockitokotlin2", "mockito-kotlin", "2.2.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testImplementation("com.h2database", "h2", "1.4.200")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.0")
}

application {
    mainClassName = "space.votebot.bot.LauncherKt"
}



tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "12"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "12"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

    "shadowJar"(ShadowJar::class) {
        archiveFileName.set("bot.jar")
    }

    test {
        useJUnitPlatform()
    }
}