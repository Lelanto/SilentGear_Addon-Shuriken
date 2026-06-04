pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases")
    }
}

plugins {
    id("net.neoforged.gradle.userdev") version "7.0.165"
}

rootProject.name = "sgShurikenAddon"