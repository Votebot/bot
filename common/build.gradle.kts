plugins {
    kotlin("jvm")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    compile("ch.qos.logback:logback-classic:1.3.0-alpha5")
    compile("com.orbitz.consul:consul-client:1.3.9")
}
