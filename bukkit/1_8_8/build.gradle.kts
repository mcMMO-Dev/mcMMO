import Config.Libs.Bukkit.`1_8` as Bukkit

plugins {
    java
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
    compileOnly(Bukkit.api) // Bukkit API for 1.8.8 - Defined in <root>/buildSrc/src/main/java/Config.kt
    compileOnly(Bukkit.nms) // CraftBukkit-1.8.8-R0.3-SNAPSHOT - Defined in <root>/buildSrc/src/main/java/Config.kt
    compileOnly(Bukkit.wgLegacy) // Old worldguard - Defined in <root>/buildSrc/src/main/java/Config.kt

}
