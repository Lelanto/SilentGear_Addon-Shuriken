pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases")
    }
}

plugins {
    // Sposta la gestione della versione del plugin qui, leggendola da gradle.properties
    id("net.neoforged.gradle.userdev") version "7.0.165"
}

rootProject.name = "sgShurikenAddon"