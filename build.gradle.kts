buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }

}

group = properties["pluginGroup"]!!
version = properties["pluginVersion"]!!

plugins {
    `java-library`
    java
}

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

}
