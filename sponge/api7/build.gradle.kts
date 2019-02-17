import net.minecraftforge.gradle.user.UserBaseExtension
import Config.Libs.Sponge.API7 as API7

buildscript {
    repositories {
        jcenter()
        maven(Repos.forge)
    }
    dependencies {
        classpath(Plugins.FG2_3.classpath)
    }
}
// Extension created to set up the minecraft block for ForgeGradle. This should change in FG3.
val Project.minecraft: UserBaseExtension
    get() = extensions.getByName<UserBaseExtension>(Plugins.FG2_3.extensionName)

plugins {
    `java-library`
    // Apply the spongegradle plugin to generate the metadata file
    id(Config.Libs.Sponge.API7.spongeGradleId) version Config.Libs.Sponge.API7.spongeGradleVersion // supplies sponge repo and plugin metadata creation tasks
}
apply(plugin = API7.forgeGradleId)

dependencies {
    compileOnly(API7.api)  // SpongeAPI
}

configure<UserBaseExtension> {
    version = API7.minecraftVersion
    runDir = "run"
    mappings = API7.mappings
}

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

