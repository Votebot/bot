plugins {
    java
    application
    kotlin("jvm") version "1.4.10"
}

group = "space.votebot"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/ktor")
    maven("https://dl.bintray.com/votebot/maven" )
    maven("https://jitpack.io")
}

dependencies {
    // Logging
    implementation("org.slf4j", "slf4j-api", "2.0.0-alpha1")
    implementation("ch.qos.logback", "logback-classic", "1.3.0-alpha5")
    implementation("io.github.microutils", "kotlin-logging", "2.0.3")

    // Sentry
    implementation("io.sentry", "sentry", "3.1.1")
    implementation("io.sentry", "sentry-logback", "3.1.1")

    // Metrics
    implementation("io.ktor:ktor-metrics-micrometer:1.4.1")

    // Ktor
    implementation("io.ktor", "ktor-server-netty", "1.4.1")
    implementation("io.ktor", "ktor-server-core", "1.4.1")
    implementation("io.ktor", "ktor-jackson", "1.4.1")
    testImplementation("io.ktor", "ktor-server-tests", "1.4.1")

    // JDA
    implementation("net.dv8tion", "JDA", "4.2.0_211") {
        exclude(module = "opus-java")
    }

    // Database
    implementation("org.jetbrains.exposed", "exposed-core", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-java-time", "0.28.1")
    implementation("com.zaxxer", "HikariCP", "3.4.5")
    implementation("org.postgresql", "postgresql", "42.2.18")

    // Util
    implementation("io.github.cdimascio", "dotenv-kotlin", "6.2.1")
    implementation("com.squareup.okhttp3", "okhttp", "4.10.0-RC1")
    implementation("xyz.downgoon", "snowflake", "1.0.0")

    // i18next
    implementation("com.i18next", "i18next-kein-android", "1.0")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.4.0-M1")

    // Tests
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit5", "1.4.10")
    testImplementation("org.mockito", "mockito-core", "3.5.15")
    testImplementation("com.nhaarman.mockitokotlin2", "mockito-kotlin", "2.2.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.0")
    testImplementation("com.h2database", "h2", "1.4.200")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.0")
    testImplementation("org.xerial", "sqlite-jdbc", "3.32.2")
}

application {
    mainClassName = "space.votebot.bot.LauncherKt"
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "13"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "13"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

    test {
        useJUnitPlatform()
    }
}