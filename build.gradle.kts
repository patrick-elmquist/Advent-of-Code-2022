import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlinx.benchmark.gradle.JvmBenchmarkTarget

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
