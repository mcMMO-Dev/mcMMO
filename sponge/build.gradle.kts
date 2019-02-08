plugins {
    java
}

val core: Project by rootProject.extra
val sponge: Project by rootProject.extra

description = "mcMMO for Sponge"

repositories {
    // sponge
    maven("https://repo.spongepowered.org/maven")
}

dependencies {
    implementation(group="org.spongepowered", name="spongeapi", version="7.1.0") // Base version
}

allprojects {
    dependencies {
        compile(core)
    }
}

subprojects {
    dependencies {
        (compile(sponge) as ModuleDependency).apply {
            exclude("org.spongepowered")
        }
    }
}

