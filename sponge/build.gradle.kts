import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import Config.Libs.Sponge as Sponge

plugins {
    java
}

val core = Projects.core!! // because it's a var and potentially null by declaration
val sponge = Projects.sponge!! // because it's a var and potentially null by declaration

description = "mcMMO for Sponge"

dependencies {
    compile(Sponge.bstats) // Bstats is used for all sponge versions
    compileOnly(Sponge.API7.api) // Base version
}

allprojects {
    dependencies {
        compile(Projects.core!!)
    }
    // TODO dunno if this works yet... project needs to compile.
    val shadowJar by tasks.getting(ShadowJar::class) {
        relocate(Deps.Groups.nossr, "${Deps.Groups.nossr}.sponge") {
            exclude("${Deps.Groups.nossr}.core")
        }
    }
}

subprojects {
    dependencies {
        (compileOnly(sponge) as ModuleDependency).apply {
            exclude(Sponge.Exclude.group, Sponge.Exclude.module)
        }
    }
}

