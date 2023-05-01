plugins {
    // kotlin since multi-version plugin depends on it
    kotlin("jvm") version("1.6.10")
    // main multi-version plugin
    id("cc.polyfrost.multi-version")
    // defaults
    id("cc.polyfrost.defaults.java")
    id("cc.polyfrost.defaults.loom")
    id("cc.polyfrost.defaults.repo")
}

val gameVersionToFabricApiVersion = mapOf(
        "1.18.2" to "0.67.0",
        "1.19.0" to "0.58.0",
        "1.19.2" to "0.67.0",
        "1.19.3" to "0.67.0",
)
val gameVersionToClothVersion = mapOf(
        "1.18.2" to "6.3.81",
        "1.19.0" to "8.2.88",
        "1.19.2" to "8.2.88",
        "1.19.3" to "9.0.94",
)
val gameVersionToModMenuVersion = mapOf(
        "1.18.2" to "3.2.5",
        "1.19.0" to "4.0.4",
        "1.19.2" to "4.1.2",
        "1.19.3" to "5.0.2",
)
group = "io.github.apricotfarmer11.mods"

repositories {
    maven("https://jitpack.io")
    maven("https://maven.shedaniel.me")
    maven("https://maven.terraformersmc.com/releases")
    maven("https://api.modrinth.com/maven")
    maven("https://repo.polyfrost.cc/releases")
    maven("https://cursemaven.com")
    mavenCentral()
    gradlePluginPortal()
}
dependencies {
    // Fabric API
    val fabricApiVersion = "${gameVersionToFabricApiVersion[platform.mcVersionStr]}+${platform.mcVersionStr}"
    modImplementation(include(fabricApi.module("fabric-api-base", fabricApiVersion)) as Any)
    modImplementation(include(fabricApi.module("fabric-networking-api-v1", fabricApiVersion)) as Any)
    modImplementation(include(fabricApi.module("fabric-resource-loader-v0", fabricApiVersion)) as Any)
    if (platform.mcVersionStr == "1.18.2") {
        modImplementation(include(fabricApi.module("fabric-command-api-v1", fabricApiVersion)) as Any)
    } else {
        modImplementation(include(fabricApi.module("fabric-command-api-v2", fabricApiVersion)) as Any)
    }
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")

    // Mod APIs
    include(modApi("me.shedaniel.cloth:cloth-config-fabric:${gameVersionToClothVersion[platform.mcVersionStr]}") {
        exclude("net.fabricmc.fabric-api")
    })
    modImplementation("com.terraformersmc:modmenu:${gameVersionToModMenuVersion[platform.mcVersionStr]}")
    // Dependencies
    include(implementation("com.github.JnCrMx:discord-game-sdk4j:0.5.5")!!)
    include(implementation("io.socket:socket.io-client:2.1.0")!!)
    include(implementation("io.socket:engine.io-client:2.1.0")!!)
    include(implementation("org.json:json:20220924")!!)
    include(implementation("org.java-websocket:Java-WebSocket:1.5.3")!!)
    if (platform.mcVersionStr == "1.18.2") {
           runtimeOnly("org.joml:joml:1.10.5")
            modRuntimeOnly("maven.modrinth:auth-me:3.1.0")
            modRuntimeOnly("maven.modrinth:sodium:mc1.19.4-0.4.11")
            modRuntimeOnly("maven.modrinth:lithium:mc1.18.2-0.10.3")
            modRuntimeOnly("maven.modrinth:lazydfu:0.1.2")
            modRuntimeOnly("maven.modrinth:entityculling:1.5.1-fabric-1.18")
            modRuntimeOnly("maven.modrinth:ferrite-core:4.2.1-fabric")
            modRuntimeOnly("maven.modrinth:dynamic-fps:v2.1.0")
            modRuntimeOnly("maven.modrinth:borderless-mining:1.1.2+1.18.2")

        //modRuntimeOnly("curse.maven:optifabric-322385:3961344")
    }
}
loom {
    accessWidenerPath.set(File("src/main/resources/tubion.accesswidener"))
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