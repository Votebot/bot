import com.google.protobuf.gradle.*

plugins {
    java
    kotlin("jvm")
    id("maven-publish")
    id("com.google.protobuf") version "0.8.10"
}

val grpcVersion = "1.26.0"

dependencies {
    implementation(kotlin("stdlib"))
    compile("com.google.protobuf:protobuf-java:3.6.1")
    compile("io.grpc:grpc-stub:$grpcVersion")
    compile("io.grpc:grpc-protobuf:$grpcVersion")
    compile("io.grpc:grpc-netty:$grpcVersion")
    if (JavaVersion.current().isJava9Compatible) {
        compile("javax.annotation:javax.annotation-api:1.3.1")
    }
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc", "build/generated/source/proto/main/java")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}