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
    implementation("com.charleskorn.kaml:kaml:0.60.0")
    implementation("org.litote.kmongo:kmongo:5.1.0")
    implementation("org.litote.kmongo:kmongo-coroutine:5.1.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}