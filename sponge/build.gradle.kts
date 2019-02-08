import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("org.spongepowered.plugin") version "0.9.0" // supplies sponge repo and plugin metadata creation tasks
    id("com.github.johnrengelman.shadow")
}
dependencies {
    compile("org.spongepowered", "spongeapi", "7.1.0")  // SpongeAPI
    compile(project(":core"))

    compile("org.bstats", "bstats-sponge", "1.4") // Sponge bstats
}

description = "mcMMO for Sponge"

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes(mapOf(
                "Implementation-Title" to "mcMMO",
                "Implementation-Version" to rootProject.properties["pluginVersion"]!!
        ))
    }
}

val shadowJar by tasks.getting(ShadowJar::class) {
    dependencies {
        include(project(":core"))
    }
}