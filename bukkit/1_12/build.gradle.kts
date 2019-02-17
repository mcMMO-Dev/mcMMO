import Config.Libs.Bukkit.`1_12` as Bukkit

plugins {
    java
}

dependencies {
    compileOnly(Bukkit.api) // Spigot API
    compileOnly(Bukkit.nms)
    compileOnly(Bukkit.wgLegacy) // WorldGuard

}
