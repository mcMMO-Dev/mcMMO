import Config.Libs as Libs

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

dependencies {

    compile(Libs.configurate) {
        exclude(Deps.Groups.guava, Deps.Modules.guava)
        exclude(Deps.Groups.checker, Deps.Modules.checker)
    }
    compile(Libs.flowmath)
    compile(Libs.jdbc)
    compile(Libs.juli)
    testCompile(Libs.junitDep)

    // Spigot for in-dev dependency
    compileOnly(Libs.Bukkit.`1_13`.spigotApi) {
        isTransitive = false
    }
}
