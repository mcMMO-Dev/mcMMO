plugins {
    java
    // Apply the spongegradle plugin to generate the metadata file
    id("org.spongepowered.plugin") version "0.9.0" // supplies sponge repo and plugin metadata creation tasks
}

dependencies {
    compile("org.spongepowered", "spongeapi", "7.1.0")  // SpongeAPI
    compile("org.bstats", "bstats-sponge", "1.4") // Sponge bstats
}

description = "mcMMO for Sponge"
