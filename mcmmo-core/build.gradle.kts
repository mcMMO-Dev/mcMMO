import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

tasks {

    shadowJar {
        /*
        dependencies {
            include(dependency("org.spongepowered:configurate-yaml"))
            include(dependency("org.spongepowered:configurate-hocon"))
            include(dependency("org.spongepowered:configurate-core"))
            include(dependency("org.bstats:bstats-bukkit"))
            include(dependency("org.apache.tomcat:tomcat-jdbc"))
            include(dependency("org.apache.tomcat:tomcat-juli"))
            include(dependency("com.typesafe:config"))
            include(dependency("co.aikar:acf-core"))
            include(dependency("co.aikar:acf-bukkit"))
            include(dependency("net.kyori:text-api"))
            include(dependency("net.kyori:text-adapter-bukkit"))
            include(dependency("net.kyori:text-serializer-gson"))
            exclude(dependency("org.spigotmc:spigot"))
        }
        */
        relocate("org.apache.commons.logging", "com.gmail.nossr50.commons.logging")
        relocate("org.apache.juli", "com.gmail.nossr50.database.tomcat.juli")
        relocate("org.apache.tomcat", "com.gmail.nossr50.database.tomcat")
        relocate("org.bstats", "com.gmail.nossr50.metrics.bstat")
        relocate("co.aikar.commands", "com.gmail.nossr50.aikar.commands")
        relocate("co.aikar.locales", "com.gmail.nossr50.aikar.locales")
        relocate("co.aikar.table", "com.gmail.nossr50.aikar.table")
        relocate("net.jodah.expiringmap", "com.gmail.nossr50.expiringmap")
        relocate("net.kyori.text", "com.gmail.nossr50.kyoripowered.text")

        mergeServiceFiles()
    }

    processResources {
        filter<ReplaceTokens>("tokens" to mapOf("project.version" to project.version))
        filesMatching("**/locales/*") {

        }
    }

    build {
        dependsOn(shadowJar)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    dependencies{
        include { true }
    }
}


dependencies {
    api(project(":mcmmo-api"))
    compile("org.apache.tomcat:tomcat-jdbc:7.0.52")
    compile("com.typesafe:config:1.3.2")
    compile("org.spongepowered:configurate-core:3.7-SNAPSHOT")
    compile("org.spongepowered:configurate-yaml:3.7-SNAPSHOT")
    compile("org.spongepowered:configurate-hocon:3.7-SNAPSHOT")
    compile("net.kyori:text-api:3.0.2")
    compile("net.kyori:text-serializer-gson:3.0.2")
    compile("net.kyori:text-adapter-bukkit:3.0.4-SNAPSHOT")
    compile("org.jetbrains:annotations:17.0.0")
    compile("org.apache.maven.scm:maven-scm-provider-gitexe:1.8.1")
    compile("org.bstats:bstats-bukkit:1.4")
    compileOnly("org.spigotmc:spigot:1.14.4-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:7.0.0-SNAPSHOT")
    testImplementation("junit:junit:4.10")
}


