import Config.Libs.Bukkit.`1_13` as Bukkit
// Config is located in <root>/buildSrc/src/main/java/Config.kt
// It provides a bunch of constant values we use as dependency
// strings, so we don't have to duplicate a bunch of them in
// various scripts. The import allows us to "import as" for shorthand

plugins {
    `java-library` // This is already provided, but for static compilation,
    // we declare it here so we can use the IDE static type references
}

/*
 Dependency inheritance is as follows
  - ":core", which provides
 configurate, tomcat jdbc/juli, and flowmath. It excludes sub
 dependencies like guava and apache commons lang.
  - ":bukkit", which provides nothing on it's own, except the
 core bukkit classes that can be built on 1.13.2 API (which may change).
 It also defines all subprojects to depend on ":core", and ":bukkit",
 and bstats-bukkit.
  */
dependencies {
    compileOnly(Bukkit.api) // Bukkit API for 1.13.2 - Defined in <root>/buildSrc/src/main/java/Config.kt
    compileOnly(Bukkit.nms) // CraftBukkit-1.13.2-R0.1-SNAPSHOT - Defined in <root>/buildSrc/src/main/java/Config.kt
    compileOnly(Bukkit.wgCore) // WorldGuard-core - Defined in <root>/buildSrc/src/main/java/Config.kt
    compileOnly(Bukkit.wgLegacy) // WorldGuard-legacy - Defined in <root>/buildSrc/src/main/java/Config.kt
}
