import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import Config.Libs.Bukkit as Bukkit

val bukkit: Project = Projects.bukkit!!
val core: Project = Projects.core!!

allprojects {

    dependencies {
        compile(core) // includes junit for tests
        implementation(Bukkit.bstats) // Bukkit bstats
    }

    val shadowJar by tasks.getting(ShadowJar::class) {
        relocate(Shadow.Origin.bstatsBukkit, Shadow.Target.bstatsBukkit)
    }
}

subprojects {
    dependencies {
        // Provide the base bukkit plugin dependency for plugin classloading.
        // All "versioned" implementations will be properly classloaded by the bukkit parent
        compileOnly(bukkit)
    }
}
plugins {
    java
}
dependencies {
    // Temporary dependencies while things are being moved.
    compileOnly(Bukkit.`1_13`.spigotApi) { // Spigot API
        isTransitive = true
    }
    compileOnly(Bukkit.`1_13`.api) { // Spigot API
        isTransitive = true

    }
    compileOnly(Bukkit.`1_13`.wgCore) {
        isTransitive = true
        exclude(group = Shadow.Exclude.sk89q)
        exclude(group = Shadow.Exclude.intake, module = "intake")
        exclude(group = Shadow.Exclude.sk89q, module = "squirrelid")
        exclude(group = Shadow.Exclude.flyway)
        exclude(group = Shadow.Exclude.khelekore)
        exclude(group = Shadow.Exclude.findbugs)
    }
    compileOnly(Bukkit.`1_13`.wgLegacy) {
        isTransitive = true
        exclude(group = Shadow.Exclude.bukkit)
        exclude(group = Shadow.Exclude.sk89q, module = "commandbook")
        exclude(group = Shadow.Exclude.bstats)
    }
}
