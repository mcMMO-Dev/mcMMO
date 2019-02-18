import Config.Libs.Bukkit.`1_12` as Bukkit
// Config is located in <root>/buildSrc/src/main/java/Config.kt
// It provides a bunch of constant values we use as dependency
// strings, so we don't have to duplicate a bunch of them in
// various scripts.

plugins {
    java // This is already provided, but for static compilation,
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
    compileOnly(Bukkit.api) // Bukkit API for 1.12.2
    compileOnly(Bukkit.nms) // CraftBukkit for 1.12.2
    compileOnly(Bukkit.wgLegacy) // WorldGuard for 1.12.2 bukkit

}
