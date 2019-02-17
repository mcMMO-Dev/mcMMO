import Config.Libs.Bukkit.`1_8` as Bukkit

plugins {
    java
}

dependencies {
    compileOnly(Bukkit.api) // Spigot API
    compileOnly(Bukkit.wgLegacy) // Old worldguard
    compileOnly(Bukkit.nms)

}
