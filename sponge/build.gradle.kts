buildscript {
    repositories {
        maven("https://repo.spongepowered.org/maven")
    }
}

plugins {
    java
    id("org.spongepowered.plugin") version "0.9.0"
}
dependencies {
    compile("org.spongepowered", "spongeapi", "7.1.0")  // SpongeAPI

    implementation("org.bstats", "bstats-sponge", "1.4") // Sponge bstats
}

description = "mcMMO for Sponge"

val compileJava by tasks.getting(JavaCompile::class) {
    options.encoding = "UTF-8"
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes(mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
        ))
    }
}