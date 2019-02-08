import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

buildscript {
    repositories {
        jcenter()
    }
}

// Extras
var core by extra { project("core") }
// Bukkit/Spigot plugins
val bukkit by extra { project("bukkit") }
val bukkit_18 by extra { bukkit.project("1_8_8") }
val bukkit_112 by extra { bukkit.project("1_12") }
val bukkit_113 by extra { bukkit.project("1_13") }

// Sponge plugins
val sponge by extra { project("sponge") }
val sponge_7 by extra { sponge.project("api7") }


group = properties["pluginGroup"]!!
version = properties["pluginVersion"]!!

plugins {
    `java-library`
    java
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

// Set up defaults for all projects, maven repositories, java compatibility level and compiling encoding
allprojects {
    apply(plugin="java-library")
    apply(plugin="com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        // World Edit
        maven("https://maven.sk89q.com/repo")
        // bStats
        maven("https://repo.codemc.org/repository/maven-public")
    }

    dependencies {
        compile("org.apache.tomcat", "tomcat-jdbc", "7.0.52") // tomcat JDBC
        compile("org.apache.tomcat", "tomcat-juli", "7.0.52") // tomcat juli
        testCompile("junit", "junit", "4.12")
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.getting(JavaCompile::class) {
        options.encoding = "UTF-8"
    }
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes(mapOf(
                "Implementation-Title" to "mcMMO",
                "Implementation-Version" to rootProject.properties["pluginVersion"]!!,
                "Main-Class" to "com.gmail.nossr50.mcMMO" // Main plugin class for bukkit
        ))
    }
}

val shadowJar by tasks.getting(ShadowJar::class) {
    dependencies {
        include(project("core"))
        include(dependency("org.bstats:bstats-bukkit:1.4"))
    }
    relocate("org.bstats", "com.gmail.nossr50.metrics.bstat")
}
