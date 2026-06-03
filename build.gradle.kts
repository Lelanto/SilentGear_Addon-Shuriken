plugins {
    java
    // Versione del plugin di sviluppo di NeoForge
    id("net.neoforged.gradle.userdev") version "7.0.165"
}

// Mappatura automatica delle variabili dal gradle.properties
val minecraft_version: String by project
val mod_version: String by project
val mod_group: String by project
val mod_id: String by project
val neoforge_version: String by project
val sg_project_id: String by project
val sg_file_id: String by project
val sl_project_id: String by project
val sl_file_id: String by project

version = "$minecraft_version-$mod_version"
group = mod_group

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
    maven {
        name = "CurseMaven"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    implementation("net.neoforged:neoforge:${neoforge_version}")

    // Silent Gear & Silent Lib via CurseMaven
    implementation("curse.maven:silent-gear-${sg_project_id}:${sg_file_id}")

    // Silent Lib (ID Progetto reale: 242998)
    implementation("curse.maven:silent-lib-${sl_project_id}:${sl_file_id}")
}

// ── BLOCCO FUNZIONANTE PER DIRE A MINECRAFT DI CARICARE I JSON IN SVILUPPO ──
configure<ExtensionAware> {
    extensions.findByName("minecraft")?.let { mc ->
        val runs = (mc as ExtensionAware).extensions.getByName("runs") as NamedDomainObjectContainer<*>

        // Forza la configurazione del client a leggere i file dentro la cartella resources
        runs.maybeCreate("client").let { client ->
            // Il plugin associa automaticamente il sourceSets.main di default
        }
    }
}
// ─────────────────────────────────────────────────────────────────────────────

tasks.processResources {
    val props = mapOf(
        "version" to mod_version,
        "mc_version" to minecraft_version
    )
    inputs.properties(props)
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(props)
    }
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