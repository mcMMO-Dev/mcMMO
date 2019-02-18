import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import Config.Libs.Sponge as Sponge
// Config is located in <root>/buildSrc/src/main/java/Config.kt
// It provides a bunch of constant values we use as dependency
// strings, so we don't have to duplicate a bunch of them in
// various scripts. The import as allows for shorthand.

plugins {
    `java-library` // This is already provided, but for static compilation,
    // we declare it here so we can use the IDE static type references
}

val core = Projects.core!! // because it's a var and potentially null by declaration
val sponge = Projects.sponge!! // because it's a var and potentially null by declaration

description = "mcMMO for Sponge"

/*
These dependencies are minimalized. SpongeAPI is not inherited by subprojects.
Bstats-sponge is api version agnostic for the moment.
 */
dependencies {
    compile(Sponge.bstats) // Bstats is used for all sponge versions
    compileOnly(Sponge.API7.api) // Base version for common plugin class
}

/* This configures ":sponge" and it's dependent projects:
   - ":sponge:api7"
   Basically sets up all projects to depend on ":core" and
   bstats-sponge. Bstatss-sponge should not be relocated
 */
allprojects {
    dependencies {
        compile(Projects.core!!)
    }
    // TODO dunno if this works yet... project needs to compile.
    val shadowJar by tasks.getting(ShadowJar::class) { // We set this up so we relocate all sponge projects, not just ":sponge"
        relocate(Deps.Groups.nossr, "${Deps.Groups.nossr}.sponge") {
            exclude("${Deps.Groups.nossr}.core")
        }
    }
}

// Tells all subprojects of ":sponge" (":sponge:api7")
// to depend on this project (":sponge") to inherit the dependencies, and
// does NOT inherit the same configurations (anything configured outside
// here does not persist to child projects).
subprojects {
    dependencies {
        (compileOnly(sponge) as ModuleDependency).apply {
            exclude(Sponge.Exclude.group, Sponge.Exclude.module)
        }
    }
}

