buildscript {
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:4.0.4")
    }
}

group = properties["pluginGroup"]!!
version = properties["pluginVersion"]!!

plugins {
    `java-library`
    java
}

// Set up defaults for all projects, maven repositories, java compatibility level and compiling encoding
allprojects {

    apply(plugin="java-library")

    repositories {
        mavenCentral()
        // World Edit
        maven("https://maven.sk89q.com/repo")
        // bStats
        maven("https://repo.codemc.org/repository/maven-public")
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.getting(JavaCompile::class) {
        options.encoding = "UTF-8"
    }

}
