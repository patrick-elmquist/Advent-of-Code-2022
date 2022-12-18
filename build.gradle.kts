import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0-RC"
}

group = "dev.patrickelmquist"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0-RC")
    implementation("io.ktor:ktor-client-java:2.2.1")
    implementation("io.ktor:ktor-client-logging:2.2.1")
    implementation("ch.qos.logback:logback-classic:1.4.5")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.time.ExperimentalTime"
    kotlinOptions.freeCompilerArgs += "-Xuse-k2"
}
