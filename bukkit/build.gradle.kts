import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import Config.Libs.Bukkit as Bukkit
// Config is located in <root>/buildSrc/src/main/java/Config.kt
// It provides a bunch of constant values we use as dependency
// strings, so we don't have to duplicate a bunch of them in
// various scripts. The import as allows for shorthand.

val bukkit: Project = Projects.bukkit!! // Static project references
val core: Project = Projects.core!! // Stored by Config.kt and created in <root>/build.gradle.kts

/* This configures ":bukkit" and it's dependent projects:
   - ":bukkit:1_8_8"
   - ":bukkit:1_12"
   - ":bukkit:1_13"
   Basically sets up all projects to depend on ":core" and
   bstats-bukkit. Also sets up shadow to relocate bukkit related
   packages to limit platform interference
 */
allprojects {

    dependencies {
        compile(core) // includes junit for tests
        implementation(Bukkit.bstats) // Bukkit bstats
    }

    // TODO dunno if this works yet... project needs to compile.
    val shadowJar by tasks.getting(ShadowJar::class) {
        // Relocate bstats for bukkit, as per requirement for bstats
        relocate(Shadow.Origin.bstatsBukkit, Shadow.Target.bstatsBukkit)
        // Relocate the bukkit platform classes of mcmmo so we don't
        // interfere with other platform classes (or core)
        relocate(Deps.Groups.nossr, "${Deps.Groups.nossr}.bukkit") {
            exclude("${Deps.Groups.nossr}.core")
        }
    }
}

// Tells all subprojects of ":bukkit" (":bukkit:1_8_8", ":bukkit:1_12",etc.)
// to depend on this project (":bukkit") to inherit the dependencies, and
// does NOT inherit the same configurations (anything configured outside
// here does not persist to child projects).
subprojects {
    dependencies {
        // Provide the base bukkit plugin dependency for plugin classloading.
        // All "versioned" implementations will be properly classloaded by the bukkit parent
        compileOnly(bukkit)
    }
}
plugins {
    `java-library` // This is already provided, but for static compilation,
    // we declare it here so we can use the IDE static type references
}
dependencies {
    // Temporary dependencies while things are being moved.
    compileOnly(Bukkit.`1_13`.spigotApi) { // Spigot API for generic usage. Based on 1.13.2
        isTransitive = true // We don't want the dependencies
    }
    compileOnly(Bukkit.`1_13`.api) { // Bukkit API for generic usage. Based on 1.13.2
        isTransitive = true // We don't want the dependencies

    }
    compileOnly(Bukkit.`1_13`.wgCore) { // WorldGuard dependency, again, for 1.13.2
        isTransitive = true // We don't want the dependencies
        exclude(group = Shadow.Exclude.sk89q)
        exclude(group = Shadow.Exclude.intake, module = "intake")
        exclude(group = Shadow.Exclude.sk89q, module = "squirrelid")
        exclude(group = Shadow.Exclude.flyway)
        exclude(group = Shadow.Exclude.khelekore)
        exclude(group = Shadow.Exclude.findbugs)
    }
    compileOnly(Bukkit.`1_13`.wgLegacy) {
        isTransitive = true // We don't want the dependencies
        exclude(group = Shadow.Exclude.bukkit)
        exclude(group = Shadow.Exclude.sk89q, module = "commandbook")
        exclude(group = Shadow.Exclude.bstats)
    }
}
