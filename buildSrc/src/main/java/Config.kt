@file:Suppress("MayBeConstant", "unused")

import org.gradle.api.Project

object Plugins {
    const val spongeGradleId = "${Deps.Groups.sponge}.plugin"

    object FG2_3 {
        const val classpath = "net.minecraftforge.gradle:ForgeGradle:${Versions.fg23}"
        const val extensionName = "minecraft"
        const val id = "net.minecraftforge.gradle.forge"
    }
}

object Repos {
    const val sk89q = "https://maven.sk89q.com/repo"
    // bStats
    const val bstats = "https://repo.codemc.org/repository/maven-public"
    // configurate
    const val sponge = "https://repo.spongepowered.org/maven/"
    const val spigot = "https://hub.spigotmc.org/nexus/content/repositories/snapshots"
    const val sonatype = "https://oss.sonatype.org/content/repositories/snapshots"
    const val forge = "https://files.minecraftforge.net/maven/"
}

object Config {

    object Libs {
        const val configurate = "${Deps.Groups.sponge}:${Deps.Modules.configurate_yaml}:${Versions.configurate}"
        const val jdbc = "${Deps.Groups.tomcat}:${Deps.Modules.jdbc}:${Versions.jdbc}"
        const val juli = "${Deps.Groups.tomcat}:${Deps.Modules.juli}:${Versions.jdbc}"
        const val junitDep = "${Deps.Groups.junit}:${Deps.Modules.junit}:${Versions.junit}"
        const val flowmath = "${Deps.Groups.flowpowered}:${Deps.Modules.flowmath}:${Versions.flowmath}"

        object Bukkit {
            object `1_8` {
                const val api = "$bukkit:${Versions.bukkit18}"
                const val spigotApi = "${Bukkit.spigotApi}:${Versions.bukkit18}"
                const val spigot = "$${Bukkit.spigot}:${Versions.bukkit18}"
                const val nms = "$craftbukkit:${Versions.bukkit18}"
                // only legacy existed at this point, no core.
                const val wgLegacy = "${Bukkit.wgLegacy}:${Versions.wg18}"
            }

            object `1_12` {
                const val api = "$bukkit:${Versions.bukkit112}"
                const val spigotApi = "${Bukkit.spigotApi}:${Versions.bukkit112}"
                const val spigot = "$${Bukkit.spigot}:${Versions.bukkit112}"
                const val nms = "$craftbukkit:${Versions.bukkit112}"
                // only legacy existed at this point, no core.
                const val wgLegacy = "${Bukkit.wgLegacy}:${Versions.wg112}"
            }

            object `1_13` {
                const val api = "$bukkit:${Versions.bukkit113}"
                const val spigotApi = "${Bukkit.spigotApi}:${Versions.bukkit113}"
                const val spigot = "$${Bukkit.spigot}:${Versions.bukkit113}"
                const val nms = "$craftbukkit:${Versions.bukkit113}"
                const val wgCore = "${Bukkit.wgCore}:${Versions.wg113}"
                const val wgLegacy = "${Bukkit.wgLegacy}:${Versions.wg113}"
            }

            const val bukkit = "${Deps.Groups.bukkit}:${Deps.Modules.bukkit}"
            const val craftbukkit = "${Deps.Groups.bukkit}:${Deps.Modules.craftbukkit}"
            const val wgCore = "${Deps.Groups.worldguard}:${Deps.Modules.wgCore}"
            const val wgLegacy = "${Deps.Groups.worldguard}:${Deps.Modules.wgLegacy}"
            const val spigotApi = "${Deps.Groups.spigot}:${Deps.Modules.spigotApi}"
            const val spigot = "${Deps.Groups.spigot}:${Deps.Modules.spigot}"
            const val bstats = "${Deps.Groups.bstats}:${Deps.Modules.bstatsBukit}:${Versions.bstats}"
        }

        object Sponge {
            object API7 { // All of these are specific to the API7 module, API8 will change
                const val forgeGradleId = "net.minecraftforge.gradle.forge"
                const val spongeGradleId = "${Deps.Groups.sponge}.plugin"
                const val spongeGradleVersion = "0.9.0"
                const val api = "${Sponge.api}:${Versions.sapi7}"
                const val common = "${Sponge.common}:${Versions.spongeImpl7}"
                const val forge_version = "14.23.5.2768"
                const val minecraftVersion = "1.12.2-$forge_version"
                const val mappings = "snapshot_20180808"
            }

            object Exclude {
                const val group = Deps.Groups.sponge
                const val module = Deps.Modules.spongeAPI
            }

            const val api = "${Deps.Groups.sponge}:${Deps.Modules.spongeAPI}"
            const val common = "${Deps.Groups.sponge}:${Deps.Modules.spongecommon}"
            const val bstats = "${Deps.Groups.bstats}:${Deps.Modules.bstatsSponge}:${Versions.bstats}"
        }
    }
}

object Deps {
    object Groups {
        const val nossr = "com.gmail.nossr50"
        const val google = "com.google"
        const val guava = "com.google.guava"
        const val gson = "com.google.code.gson"
        const val yaml = "org.yaml"
        const val sk89q = "com.sk89q"
        const val apache = "org.apache"
        const val worldguard = "$sk89q.worldguard"
        const val worldedit = "$sk89q.worldedit"
        const val sponge = "org.spongepowered"
        const val spigot = "org.spigotmc"
        const val md5 = "net.md_5"
        const val bukkit = "org.bukkit"
        const val bstats = "org.bstats"
        const val tomcat = "org.apache.tomcat"
        const val junit = "junit"
        const val checker = "org.checkerframework"
        const val flowpowered = "com.flowpowered"
    }

    object Modules {
        const val guava = "guava"
        const val gson = "gson"
        const val snakeyaml = "snakeyaml"
        const val wgCore = "worldguard-core"
        const val wgLegacy = "worldguard-legacy"
        const val bungeecordChat = "bungeecord-chat"
        const val spongeAPI = "spongeapi"
        const val spongecommon = "spongecommon"
        const val spongeforge = "spongeforge"
        const val spongevanilla = "spongevanilla"
        const val bukkit = "bukkit"
        const val craftbukkit = "craftbukkit"
        const val bstatsBukit = "bstats-bukkit"
        const val bstatsSponge = "bstats-sponge"
        const val spigotApi = "spigot-api"
        const val spigot = "spigot"
        const val configurate = "configurate"
        const val configurate_core = "${configurate}-core"
        const val configurate_yaml = "${configurate}-yaml"
        const val jdbc = "tomcat-jdbc"
        const val juli = "tomcat-juli"
        const val junit = "junit"
        const val checker = "checker-qual"
        const val flowmath = "flow-math"
    }
}

object Projects {
    var core: Project? = null
    var bukkit: Project? = null
    var sponge: Project? = null
}

object Shadow {
    object Origin {
        const val juli = "${Deps.Groups.apache}.juli"
        const val tomcat = "${Deps.Groups.apache}.tomcat"
        const val apache = "${Deps.Groups.apache}.commons.logging"
        const val bstatsBukkit = "${Deps.Groups.bstats}.bukkit"
        const val configurate = "ninja.leaping.configurate"
        const val checker = "org.checkerframework"
    }

    object Target {
        const val juli = "${Deps.Groups.nossr}.database.tomcat.juli"
        const val tomcat = "${Deps.Groups.nossr}.database.tomcat"
        const val apache = "${Deps.Groups.nossr}.commons.logging"
        const val bstatsBukkit = "${Deps.Groups.nossr}.metrics.bstat"
        const val configurate = "${Deps.Groups.nossr}.${Deps.Modules.configurate}"
        const val checker = "${Deps.Groups.nossr}.${Deps.Modules.configurate}.checkerframework"
    }

    object Exclude {
        const val tomcat = "${Deps.Groups.tomcat}:${Deps.Modules.jdbc}"
        const val juli = "${Deps.Groups.apache}:${Deps.Modules.juli}"
        const val guava = "${Deps.Groups.guava}:${Deps.Modules.guava}"
        const val snakeyaml = "${Deps.Groups.yaml}:${Deps.Modules.snakeyaml}"
        const val bukkit = Deps.Groups.bukkit
        const val spigot = Deps.Groups.spigot
        const val sk89q = "com.sk89q"
        const val wg = "$sk89q.worldguard"
        const val intake = "$sk89q.intake"
        const val flyway = "com.flywaydb"
        const val khelekore = "org.khelekore"
        const val findbugs = "com.google.code.findbugs"
        const val bstats = "${Deps.Groups.bstats}"

        object ForgeGradle {
            const val dummyThing = "dummyThing"
            const val template = "Version.java.template"
        }
    }
}