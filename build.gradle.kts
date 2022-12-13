import kotlinx.benchmark.gradle.JvmBenchmarkTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0-RC"
    id("org.jetbrains.kotlinx.benchmark") version ("0.4.4")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.0-RC"
}

group = "dev.patrickelmquist"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.6")
    implementation("io.ktor:ktor-client-java:2.2.1")
    implementation("io.ktor:ktor-client-logging:2.2.1")
    implementation("ch.qos.logback:logback-classic:1.4.5")

}

benchmark {
    configurations {
        named("main") {
            iterationTime = 5
            iterationTimeUnit = "sec"

        }
    }
    targets {
        register("main") {
            this as JvmBenchmarkTarget
            jmhVersion = "1.21"
        }
    }
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }
    wrapper {
        gradleVersion = "7.6"
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.time.ExperimentalTime"
    kotlinOptions.freeCompilerArgs += "-Xuse-k2"
}
