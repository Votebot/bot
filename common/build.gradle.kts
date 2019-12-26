plugins {
    kotlin("jvm")
}


repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib"))
    compile("ch.qos.logback:logback-classic:1.3.0-alpha5")
    compile("com.orbitz.consul:consul-client:1.3.9")
    compile("io.github.cdimascio:java-dotenv:5.1.3")
    compile("io.sentry:sentry:1.7.27")
    compile("io.sentry:sentry-logback:1.7.27")
}
