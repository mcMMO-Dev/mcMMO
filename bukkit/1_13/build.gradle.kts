import Config.Libs.Bukkit.`1_13` as Bukkit

plugins {
    `java-library`
}

dependencies {
    compileOnly(Bukkit.api) // Bukkit API
    compileOnly(Bukkit.nms)
    compileOnly(Bukkit.wgCore) // WorldGuard
    compileOnly(Bukkit.wgLegacy) // WG for Bukkit
}
