plugins {
    java
    application
    kotlin("jvm") version "1.4.0"
}

group = "space.votebot"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/ktor")
    maven("https://dl.bintray.com/votebot/maven")
    maven("https://jitpack.io")
}

dependencies {
    // Logging
    implementation("org.slf4j", "slf4j-api", "2.0.0-alpha1")
    implementation("ch.qos.logback", "logback-classic", "1.3.0-alpha5")
    implementation("io.github.microutils", "kotlin-logging", "1.8.3")

    // Sentry
    implementation("io.sentry", "sentry", "1.7.30")
    implementation("io.sentry", "sentry-logback", "1.7.30")

    // Metrics
    implementation("io.ktor:ktor-metrics-micrometer:1.4.0")
    implementation("io.micrometer:micrometer-registry-prometheus:1.5.4")

    // Ktor
    implementation("io.ktor", "ktor-server-netty", "1.4.0")
    implementation("io.ktor", "ktor-server-core", "1.4.0")
    implementation("io.ktor", "ktor-jackson", "1.4.0")
    testImplementation("io.ktor", "ktor-server-tests", "1.4.0")

    // JDA
    implementation("net.dv8tion", "JDA", "4.2.0_197") {
        exclude(module = "opus-java")
    }

    // Database
    implementation("org.jetbrains.exposed", "exposed-core", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-java-time", "0.25.1")
    implementation("com.zaxxer", "HikariCP", "3.4.5")
    implementation("org.postgresql", "postgresql", "42.2.15")

    // Util
    implementation("io.github.cdimascio", "java-dotenv", "5.2.1")
    implementation("com.squareup.okhttp3", "okhttp", "4.8.0")
    implementation("xyz.downgoon", "snowflake", "1.0.0")

    // i18next
    implementation("com.i18next", "i18next-kein-android", "1.0")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.3.9")

    // Tests
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit5", "1.4.0")
    testImplementation("org.mockito", "mockito-core", "3.5.2")
    testImplementation("com.nhaarman.mockitokotlin2", "mockito-kotlin", "2.2.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.2")
    testImplementation("com.h2database", "h2", "1.4.200")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.2")
    testImplementation("org.xerial", "sqlite-jdbc", "3.31.1")
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