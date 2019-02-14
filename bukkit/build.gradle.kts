buildscript {
    repositories { jcenter() }
    dependencies { classpath("com.github.jengelman.gradle.plugins:shadow:4.0.4") }
}
val bukkit: Project by rootProject.extra
val core: Project by rootProject.extra
// This configures the bukkit/spigot ecosystem repositories, so they all share the same repos
allprojects {
    repositories {
        // Spigot & Bukkit
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        mavenLocal() // For nms variants
    }

    dependencies {
        compile(core) // includes junit for tests
        compile("org.bstats", "bstats-bukkit", "1.4") // Bukkit bstats
    }
}

subprojects {
    dependencies {
        // Provide the base bukkit plugin dependency for plugin classloading.
        // All "versioned" implementations will be properly classloaded by the bukkit parent
        (compile(bukkit) as ModuleDependency).apply { exclude("org.spigotmc") }
    }
}
plugins {
    java
}
dependencies {
    implementation("org.spigotmc:bukkit-api:1.13.2-R0.1-SNAPSHOT") // Spigot API
}
