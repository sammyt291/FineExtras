plugins {
    java
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.fineextras"
version = "1.0.3"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    // Folia API for scheduler support (optional at runtime)
    compileOnly("dev.folia:folia-api:1.20.4-R0.1-SNAPSHOT")
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveBaseName.set("FineExtras")
        archiveClassifier.set("")
        archiveVersion.set(version.toString())
        
        // Minimize the jar - remove unused classes
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        archiveClassifier.set("original")
    }
}
