
plugins {
    `java-library`
}

dependencies {
    implementation("org.spigotmc:bukkit-api:1.13.2-R0.1-SNAPSHOT") // Spigot API
    implementation("com.sk89q.worldguard", "worldguard-core", "7.0.0-SNAPSHOT") // WorldGuard
    implementation("com.sk89q.worldguard", "worldguard-legacy", "7.0.0-SNAPSHOT") // NEEDED
}
