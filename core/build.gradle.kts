plugins {
    java
}

repositories {
    // Repo containing the Configurable library
    maven("https://repo.spongepowered.org/maven")
}

dependencies {
    compile("org.spongepowered", "configurate-yaml", "3.6") // Configurable (config library from Sponge)
}
