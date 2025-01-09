plugins {
    kotlin("jvm") version "2.1.0"
}

group = "me.rejomy"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io") }
    flatDir {
        dirs("lib")
    }
}

dependencies {
    compileOnly(fileTree("lib") {
        include("*.jar")
    })

    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.7.0")
}


tasks.processResources {
    doFirst {
        file("$buildDir/main").listFiles()?.forEach { it.delete() }
    }
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "17"
}

