val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.5.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.20"
}

group = "com.retheviper"
version = "0.0.1"

application {
    mainClass.set("com.retheviper.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Modules
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Test
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion") {
        exclude("junit", "junit")
    }
    testImplementation(kotlin("test"))

    // Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.h2database:h2:1.4.199")

    // Encryption
    implementation("com.ToxicBakery.library.bcrypt:bcrypt:+")

    // Logger
    implementation("io.github.microutils:kotlin-logging:1.12.5")

}

tasks {
    test {
        useJUnitPlatform()
    }
}