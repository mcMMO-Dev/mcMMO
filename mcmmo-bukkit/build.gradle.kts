import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

tasks {

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        dependencies {
            include(dependency("org.bstats:bstats-bukkit"))
            exclude(dependency("org.spigotmc:spigot"))
        }
        relocate("org.apache.commons.logging", "com.gmail.nossr50.commons.logging")
        relocate("org.apache.juli", "com.gmail.nossr50.database.tomcat.juli")
        relocate("org.apache.tomcat", "com.gmail.nossr50.database.tomcat")
        relocate("org.bstats", "com.gmail.nossr50.metrics.bstat")
    }

    processResources {
        filter<ReplaceTokens>("tokens" to mapOf("project.version" to project.version))
    }
}


dependencies {
    api(project(":mcmmo-api"))
    implementation(project(":mcmmo-core"))

    api("org.apache.tomcat:tomcat-jdbc:7.0.52")
    api("net.kyori:event-api:3.0.0")
    implementation("org.apache.maven.scm:maven-scm-provider-gitexe:1.8.1")
    implementation("org.bstats:bstats-bukkit:1.4")
    implementation("org.spigotmc:spigot:1.14.4-R0.1-SNAPSHOT")
    implementation("com.sk89q.worldguard:worldguard-legacy:7.0.0-SNAPSHOT")
    testImplementation("junit:junit:4.10")
}


