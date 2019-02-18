import net.minecraftforge.gradle.user.UserBaseExtension
import Config.Libs.Sponge.API7 as API7
// Config is located in <root>/buildSrc/src/main/java/Config.kt
// It provides a bunch of constant values we use as dependency
// strings, so we don't have to duplicate a bunch of them in
// various scripts. The import as allows for shorthand.

/*
Special dependency for the buildscript to be able to use ForgeGradle 2.3-SNAPSHOT.
Needs to define the repository where FG exists, and then adds the classpath of the
plugin jar for the buildscript. It's what allows us to import UserBaseExtension
 */
buildscript {
    repositories {
        jcenter()
        maven(Repos.forge)
    }
    dependencies {
        classpath(Plugins.FG2_3.classpath)
    }
}

// Extension created to set up the minecraft block for ForgeGradle. This will be different for ForgeGradle 3>, but we don't
// use that newer version for 1.12, that will be used for 1.13+
val Project.minecraft: UserBaseExtension
    get() = extensions.getByName<UserBaseExtension>(Plugins.FG2_3.extensionName)

plugins {
    `java-library`
    // Apply the spongegradle plugin to generate the metadata file, these cannot be import shorthanded because
    // they need to be resolved at script compilation time to apply the plugin
    id(Config.Libs.Sponge.API7.spongeGradleId) version Config.Libs.Sponge.API7.spongeGradleVersion // supplies sponge repo and plugin metadata creation tasks
}

// Apply the FG2.3 plguin the old way, because there's no valid way to do it the new plugins way.
apply(plugin = API7.forgeGradleId)

dependencies {
    // Only SpongeAPI needed for the base plugin class, a majority will be api version dependent.
    compileOnly(API7.api)  // SpongeAPI
}

/*
Now this configures ForgeGradle to set up the Minecraft (NMS) dependency. To use it, one needs to run
`gradlew sDecW` either from root or `gradlew :sponge:api7:sDecW`. The process generates a Minecraft
dependency with forge sources so one can read MCP remapped code. The dependency is not included in
git.
 */
configure<UserBaseExtension> {
    version = API7.minecraftVersion // The minecraft (forge) version
    runDir = "run" // Where the run directory will be placed
    mappings = API7.mappings // The MCP mappings version
}

/**
 * Some extra information that needs to be included for plugin/mod generation that will be
 * parsed by Sponge or Forge (when SpongeForge is involved, Forge loads the plugins for SpongeForge)
 */
tasks.withType<Jar> {
    inputs.properties += "version" to project.version
    inputs.properties += "mcversion" to project.minecraft.version

    baseName = "mcmmo"

    filesMatching("/mcmod.info") {
        expand(mapOf(
                "version" to project.version,
                "mcversion" to project.minecraft.version
        ))
    }
}

