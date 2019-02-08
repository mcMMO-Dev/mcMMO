repositories {

    // Spigot & Bukkit
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")

    mavenLocal() // For nms variants

}
plugins {
    java
}
dependencies {
    implementation("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") // Spigot API
    implementation("com.sk89q.worldguard", "worldguard-core", "7.0.0-SNAPSHOT") // WorldGuard
    implementation("com.sk89q.worldguard", "worldguard-legacy", "7.0.0-SNAPSHOT") // NEEDED

    implementation("org.bstats", "bstats-bukkit", "1.4") // Bukkit bstats

    implementation("org.apache.tomcat", "tomcat-jdbc", "7.0.52") // tomcat JDBC
    implementation("org.apache.tomcat", "tomcat-juli", "7.0.52") // tomcat juli
    implementation("junit", "junit", "4.12")

}
java {
    sourceSets {
        create("nms")
    }

}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes(mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Main-Class" to "com.gmail.nossr50.mcMMO" // Main plugin class for bukkit
        ))
    }
}