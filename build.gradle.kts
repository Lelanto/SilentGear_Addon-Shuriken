plugins {
    java
    idea
    id("net.neoforged.moddev") version "2.0.141"
}

val minecraft_version: String by project
val minecraft_version_range: String by project
val neo_version: String by project
val neo_version_range: String by project
val loader_version_range: String by project
val mod_version: String by project
val mod_group: String by project
val mod_id: String by project
val mod_name: String by project
val mod_license: String by project
val mod_authors: String by project
val mod_description: String by project
val sg_project_id: String by project
val sg_file_id: String by project
val sl_project_id: String by project
val sl_file_id: String by project

version = mod_version
group = mod_group

base {
    archivesName.set(mod_id)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
    mavenCentral()
    maven {
        name = "CurseMaven"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

neoForge {
    version = neo_version

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }
        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(mod_id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

configurations {
    create("localRuntime")
    runtimeClasspath.configure {
        extendsFrom(configurations.named("localRuntime").get())
    }
}

dependencies {
    val silentGear = "curse.maven:silent-gear-${sg_project_id}:${sg_file_id}"
    val silentLib = "curse.maven:silent-lib-${sl_project_id}:${sl_file_id}"

    implementation(silentGear)
    implementation(silentLib)
    "localRuntime"(silentGear)
    "localRuntime"(silentLib)
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraft_version,
        "minecraft_version_range" to minecraft_version_range,
        "neo_version" to neo_version,
        "neo_version_range" to neo_version_range,
        "loader_version_range" to loader_version_range,
        "mod_id" to mod_id,
        "mod_name" to mod_name,
        "mod_license" to mod_license,
        "mod_version" to mod_version,
        "mod_authors" to mod_authors,
        "mod_description" to mod_description
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

sourceSets.main.configure {
    resources.srcDir(generateModMetadata)
}

neoForge.ideSyncTask(generateModMetadata)

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes(
            "Specification-Title" to mod_id,
            "Specification-Version" to "1",
            "Implementation-Title" to project.name,
            "Implementation-Version" to mod_version
        )
    }
}
