import gg.essential.gradle.util.noServerRunConfigs
import gg.essential.gradle.util.setJvmDefault

plugins {
    // kotlin since multi-version plugin depends on it
    kotlin("jvm") version("1.6.10")
    // main multi-version plugin
    id("gg.essential.multi-version")
    // defaults
    id("gg.essential.defaults.java")
    id("gg.essential.defaults.loom")
    id("gg.essential.defaults.repo")
    id("gg.essential.defaults.maven-publish")
}

val gameVersionToFabricApiVersion = mapOf(
        "1.18.2" to "0.67.0",
        "1.19.2" to "0.67.0",
        "1.19.3" to "0.67.0",
)
val gameVersionToClothVersion = mapOf(
        "1.18.2" to "6.3.81",
        "1.19.2" to "8.2.88",
        "1.19.3" to "9.0.94",
)
val gameVersionToModMenuVersion = mapOf(
        "1.18.2" to "3.2.5",
        "1.19.2" to "4.1.2",
        "1.19.3" to "5.0.2",
)
group = "io.github.apricotfarmer11.mods"
java.withSourcesJar()
tasks.compileKotlin.setJvmDefault("all")
loom.noServerRunConfigs()

repositories {
    maven("https://jitpack.io")
    maven("https://maven.shedaniel.me")
    maven("https://maven.terraformersmc.com/releases")
    maven("https://api.modrinth.com/maven")
    mavenCentral()
}
dependencies {
    // Fabric API
    modImplementation("net.fabricmc.fabric-api:fabric-api:${gameVersionToFabricApiVersion[platform.mcVersionStr]}+${platform.mcVersionStr}")

    // Mod APIs
    modApi("me.shedaniel.cloth:cloth-config-fabric:${gameVersionToClothVersion[platform.mcVersionStr]}") {
        exclude("net.fabricmc.fabric-api")
    }
    modImplementation("com.terraformersmc:modmenu:${gameVersionToModMenuVersion[platform.mcVersionStr]}")
    // Dependencies
    include(implementation("com.github.JnCrMx:discord-game-sdk4j:master-SNAPSHOT") as Any)
    include(implementation("io.socket:socket.io-client:2.1.0") as Any)
    if (platform.mcVersionStr == "1.18.2") {
        runtimeOnly("org.joml:joml:1.10.5")
        modRuntimeOnly("maven.modrinth:auth-me:3.1.0")
        modRuntimeOnly("maven.modrinth:sodium:mc1.18.2-0.4.1")
        modRuntimeOnly("maven.modrinth:lithium:mc1.18.2-0.10.3")
        modRuntimeOnly("maven.modrinth:lazydfu:0.1.2")
        modRuntimeOnly("maven.modrinth:entityculling:1.5.1-fabric-1.18")
        modRuntimeOnly("maven.modrinth:ferrite-core:4.2.1-fabric")
        modRuntimeOnly("maven.modrinth:dynamic-fps:v2.1.0")
    }
}
tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand(mapOf(
                    "fabricVer" to gameVersionToFabricApiVersion[platform.mcVersionStr],
                    "clothVer" to gameVersionToClothVersion[platform.mcVersionStr],
                    "mcVer" to platform.mcVersionStr,
                    "version" to project.version,
            ))
        }
    }
}