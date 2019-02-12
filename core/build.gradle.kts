plugins {
    java
}

repositories {
    // Repo containing the Configurable library
    maven("https://repo.spongepowered.org/maven")
    // Flow Math
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compile("com.flowpowered", "flow-math", "1.0.4-SNAPSHOT")
    compile("org.spongepowered", "configurate-yaml", "3.6") // Configurable (config library from Sponge)
}
