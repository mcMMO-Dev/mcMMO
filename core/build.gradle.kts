
plugins {
    java
}
dependencies {
    implementation("junit", "junit", "4.12")
}
val jar by tasks.getting(Jar::class) {
    manifest {
        attributes(mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
        ))
    }
}