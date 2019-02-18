import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

/*
 * Declares the version of the Gradle wrapper. We need 4.9 for now because
 * ForgeGradle 3+ is a hard dependency for Gradle 4.9, 4.10 is not compatible
 */
val wrapper by tasks.getting(Wrapper::class) {
  gradleVersion = "4.9"
}

/*
 * Sets up project references to be used in child scripts, like
 * ":bukkit", ":core", ":sponge" where these projects need to be
 * referred to for dependencies, paths, outputs etc.
 * Projects is specifically an object stored in <root>/buildSrc/src/main/java/Config.kt
 * It's a nullable variable, but we just store it here and use it elsewhere.
 */
Projects.core = project("core")
Projects.bukkit = project("bukkit")
Projects.sponge = project("sponge")
/*
Declares the various other projects and stores them to Gradle's `extra` properties.
These are potentially usable for other purposes, but for now, they're here only to
declare the values for this root project's dependency (for shadowjar)
 */
var core: Project by extra { project("core") }
val bukkit by extra { project("bukkit") }
val bukkit_18 by extra { bukkit.project("1_8_8") }
val bukkit_112 by extra { bukkit.project("1_12") }
val bukkit_113 by extra { bukkit.project("1_13") }
val sponge by extra { project("sponge") }
val sponge_7 by extra { sponge.project("api7") }

group = properties["pluginGroup"]!!
version = properties["pluginVersion"]!!

/*
Even though all projects declares some of these plugins, we want to declare them the traditional
way so that we can have IDE utiliziation and processing, it helps with writing these scripts.
 */
plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

/*
Default management for ALL projects, not just root, or ":bukkit", but all projects and
their children projects.
 */
allprojects {
    /*
    We need the java library processing, and shadow allows us to run
    shadowJar to relocate dependencies and bundle dependencies into a fat jar.
     */
    apply(plugin="java-library")
    apply(plugin="com.github.johnrengelman.shadow")

    /*
    Defines all the repositories for all project dependency resolutions. Some of these
    repositories are meant for specific dependencies, so the content filters will
    prevent attempts at resolving those dependencies being requested at those repositories.
    Constants are defined in <root>/buildSrc/src/main/java/Config.kt
     */
    repositories {
        mavenCentral()
        maven(Repos.sk89q) // WorldEdit/WorldGuard
        maven(Repos.bstats) // bstats
        maven(Repos.sponge) // Sponge, Configurate, and some other things
        maven(Repos.spigot) // Spigot and Bukkit
        maven(Repos.sonatype) // General Maven
        mavenLocal() // For nms packages
    }

    // Sets all projects compatibility level to Java 8
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    // Encoding for all packages is UTF-8
    tasks.getting(JavaCompile::class) {
        options.encoding = "UTF-8"
    }
    // Default shadow jar configuration. Sub projects will override and add on,
    // but this sets up at the very least the jdbc connection dependencies to be relocated
    val shadowJar by tasks.getting(ShadowJar::class) { // Configure basics of relocation
        relocate(Shadow.Origin.juli, Shadow.Target.juli)
        relocate(Shadow.Origin.tomcat, Shadow.Target.tomcat)
        exclude(Shadow.Exclude.ForgeGradle.dummyThing)
        exclude(Shadow.Exclude.ForgeGradle.template)
    }

}

/*
All subprojects shadowjar tasks that will exclude various dependencies, while
the root project will include some of these dependencies (like jdbc, configurate)
so that the sub project jars are already somewhat minimized, in the event those
platform jars are to be deployed individually versus an overall "all platforms"
jar.
 */
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

// Sets up this root project to depend on all the implementations supported.
// By default, they all already should have shadow relocations and packaging,
// and their dependencies should not be leaking into this project.
dependencies {
    compile(bukkit)
    compile(sponge)
    compile(bukkit_18)
    compile(bukkit_112)
    compile(bukkit_113)
    compile(sponge_7)
}

// Configure shadow for the root project, we want to relocate bstats-bukkit
// and whatever else is configured in the allProjects configuration
val shadowJar by tasks.getting(ShadowJar::class) { // Root shadow relocation

    relocate(Shadow.Origin.bstatsBukkit, Shadow.Target.bstatsBukkit)

    baseName = "mcMMO"
    classifier = "bundle"
}

// Tell the build task to depend on shadowjar.
val build by tasks
build.dependsOn(shadowJar)
