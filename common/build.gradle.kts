plugins {
    kotlin("jvm")
}


repositories {
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib"))
    api("io.github.microutils:kotlin-logging:1.7.8")
    api("ch.qos.logback:logback-classic:1.3.0-alpha5")
    api("com.orbitz.consul:consul-client:1.3.9")
    api("io.github.cdimascio:java-dotenv:5.1.3")
    api("io.sentry:sentry:1.7.27")
    api("io.sentry:sentry-logback:1.7.27")
}
