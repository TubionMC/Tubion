pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.polyfrost.cc/releases")
        maven("https://maven.architectury.dev")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net")
        maven("https://jitpack.io")
    }
    plugins {
        id("cc.polyfrost.multi-version.root") version "0.1.27"
    }
}

listOf(
        "1.18.2-fabric",
        "1.19.2-fabric",
        "1.19.3-fabric"
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}

rootProject.buildFileName = "root.gradle.kts"