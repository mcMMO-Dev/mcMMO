import Config.Libs as Libs
// Config is located in <root>/buildSrc/src/main/java/Config.kt
// It provides a bunch of constant values we use as dependency
// strings, so we don't have to duplicate a bunch of them in
// various scripts. The import as allows for shorthand.

plugins {
    `java-library` // This is already provided, but for static compilation,
    // we declare it here so we can use the IDE static type references
}

dependencies {

    compile(Libs.configurate) { // Configurate-Yaml dependency, inherits Configurate-core
        exclude(Deps.Groups.guava, Deps.Modules.guava) // Exclude guava
        exclude(Deps.Groups.checker, Deps.Modules.checker) // Exclude checkerframework
    }
    compile(Libs.flowmath) // flowpowered math, for more maths.
    compile(Libs.jdbc) // Database connectors
    compile(Libs.juli) // Database connectors
    testCompile(Libs.junitDep) // junit for testing

    // Spigot for in-dev dependency
    compileOnly(Libs.Bukkit.`1_13`.spigotApi) { // Spigot only for temporary usage in core
        isTransitive = false // Don't include spigot api's dependencies
    }
}
