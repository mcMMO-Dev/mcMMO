subprojects {
    apply(plugin = "java-library")
    // Java is not explicitly needed, but keeps IJ happy with the tasks block
    apply(plugin = "java")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.spongepowered.org/maven")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://maven.sk89q.com/repo")
        maven("https://mvnrepository.com/artifact/org.jetbrains/annotations")
        maven("https://repo.aikar.co/content/groups/aikar/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    }

    tasks {
        withType(JavaCompile::class.java) {
            options.encoding = "UTF-8"
        }
    }

    configure<JavaPluginConvention> {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

}
