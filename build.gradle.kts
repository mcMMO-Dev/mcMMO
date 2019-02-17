import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

buildscript {
    repositories {
        jcenter()
        maven("https://files.minecraftforge.net/maven/")
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT")
    }
}

// Things used by other projects
Projects.core = project("core")
Projects.bukkit = project("bukkit")
Projects.sponge = project("sponge")
var core: Project by extra { project("core") }
val bukkit by extra { project("bukkit") }
val bukkit_18 by extra { bukkit.project("1_8_8") }
val bukkit_112 by extra { bukkit.project("1_12") }
val bukkit_113 by extra { bukkit.project("1_13") }
val sponge by extra { project("sponge") }
val sponge_7 by extra { sponge.project("api7") }

val configurate by extra { ""}

group = properties["pluginGroup"]!!
version = properties["pluginVersion"]!!

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

configurations {
    create("childJars")
}
val childJars: Configuration by configurations


// Set up defaults for all projects, maven repositories, java compatibility level and compiling encoding
allprojects {
    apply(plugin="java-library")
    apply(plugin="com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        // World Edit
        maven(Repos.sk89q)
        // bStats
        maven(Repos.bstats)
        // configurate
        maven(Repos.sponge)
        // spigot
        maven(Repos.spigot)
        maven(Repos.sonatype)
        mavenLocal()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.getting(JavaCompile::class) {
        options.encoding = "UTF-8"
    }
    val shadowJar by tasks.getting(ShadowJar::class) { // Configure basics of relocation
        relocate(Shadow.Origin.juli, Shadow.Target.juli)
        relocate(Shadow.Origin.tomcat, Shadow.Target.tomcat)
        exclude(Shadow.Exclude.ForgeGradle.dummyThing)
        exclude(Shadow.Exclude.ForgeGradle.template)
    }

}

// Sub projects don't need to shadow their dependencies. This eliminates common ones
subprojects {
    val shadowJar by tasks.getting(ShadowJar::class) {
        dependencies {
            exclude(dependency("${Deps.Groups.sponge}:${Deps.Modules.configurate_yaml}"))
            exclude(dependency(Shadow.Exclude.guava))
            exclude(dependency(Shadow.Exclude.snakeyaml))
            exclude(dependency(Shadow.Exclude.tomcat))
            exclude(dependency(Shadow.Exclude.juli))
        }
    }
}

dependencies {
    compile(bukkit)
    compile(sponge)
    compile(bukkit_18)
    compile(bukkit_112)
    compile(bukkit_113)
    compile(sponge_7)
}
val shadowJar by tasks.getting(ShadowJar::class) { // Root shadow relocation

    relocate(Shadow.Origin.bstatsBukkit, Shadow.Target.bstatsBukkit)

    baseName = "mcMMO"
    classifier = "bundle"
}
val build by tasks
build.dependsOn(shadowJar)
