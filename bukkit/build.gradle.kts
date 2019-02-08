import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:4.0.4")
    }
}

repositories {

    // Spigot & Bukkit
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")

    mavenLocal() // For nms variants
}
plugins {
    java
    id("com.github.johnrengelman.shadow")
}
dependencies {
    implementation("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") // Spigot API
    implementation("com.sk89q.worldguard", "worldguard-core", "7.0.0-SNAPSHOT") // WorldGuard
    implementation("com.sk89q.worldguard", "worldguard-legacy", "7.0.0-SNAPSHOT") // NEEDED

    compile("org.bstats", "bstats-bukkit", "1.4") // Bukkit bstats

    implementation("org.apache.tomcat", "tomcat-jdbc", "7.0.52") // tomcat JDBC
    implementation("org.apache.tomcat", "tomcat-juli", "7.0.52") // tomcat juli
    implementation("junit", "junit", "4.12")

    compile(project(":core"))
}
java {
    sourceSets {
        create("nms")
    }
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes(mapOf(
                "Implementation-Title" to "mcMMO",
                "Implementation-Version" to rootProject.properties["pluginVersion"]!!,
                "Main-Class" to "com.gmail.nossr50.mcMMO" // Main plugin class for bukkit
        ))
    }
}

val shadowJar by tasks.getting(ShadowJar::class) {
    dependencies {
        include(project(":core"))
        include(dependency("org.bstats:bstats-bukkit:1.4"))
    }
    relocate("org.bstats", "com.gmail.nossr50.metrics.bstat")
}