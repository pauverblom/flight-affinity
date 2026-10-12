pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        exclusiveContent {
            forRepository { maven("https://maven.fabricmc.net/") { name = "Fabric" } }
            filter { includeGroupAndSubgroups("net.fabricmc"); includeGroup("fabric-loom") }
        }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "flight-affinity"

/*
 * Every directory in versions/ is one build target, named "<minecraft>-<loader>".
 * Its gradle.properties holds the dependency versions and the list of Minecraft
 * versions the resulting jar is published for. Adding a target = adding a folder.
 */
val targets = file("versions").listFiles()!!
    .filter { File(it, "gradle.properties").isFile }
    .map { it.name }
    .sorted()

stonecutter {
    create(rootProject) {
        for (target in targets) {
            val minecraft = target.substringBeforeLast('-')
            val loader = target.substringAfterLast('-')
            version(target, minecraft).buildscript("build.$loader.gradle")
        }
        // The version whose code is "uncommented" in src/ when committing.
        vcsVersion = "26.1.2-fabric"
    }
}
