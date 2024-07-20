plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "1.4.20"
}

group = "ru.airdead.iwseller"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.16.5-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.xerial:sqlite-jdbc:3.34.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
