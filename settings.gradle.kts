import java.util.Properties

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
 * Every directory in versions/ is one target, named "<minecraft>-<loader>". There are two kinds:
 *  - release targets build a jar; mc_targets lists the Minecraft versions that jar is published for.
 *  - smoke targets (smoke_of=<release target>) run the GameTests on one other Minecraft version
 *    with that release target's jar installed, so every published version is actually tested.
 * "<minecraft>-<loader>-<other loader>" runs a release target's code on another loader
 * (Forge 1.20.1 on NeoForge 1.20.1).
 */
val targets = file("versions").listFiles()!!
    .filter { File(it, "gradle.properties").isFile }
    .map { it.name }
    .sorted()

fun props(target: String) = Properties().apply { file("versions/$target/gradle.properties").inputStream().use(::load) }
val targetProps = targets.associateWith(::props)

// Every published (Minecraft version, loader) pair must have a target that tests it.
val problems = mutableListOf<String>()
for ((target, p) in targetProps) {
    val (minecraft, loader) = target.split('-')
    val smokeOf = p.getProperty("smoke_of")
    if (smokeOf == null) {
        val declared = p.getProperty("mc_targets").trim().split(Regex("\\s+"))
        val expected = declared.filter { it != minecraft }.map { "$it-$loader" } +
            (p.getProperty("also_runs_on") ?: "").split(' ').filter { it.isNotBlank() }.map { "$minecraft-$loader-$it" }
        for (smoke in expected) {
            if (targetProps[smoke]?.getProperty("smoke_of") != target)
                problems += "$target publishes for '$smoke' but versions/$smoke/gradle.properties with smoke_of=$target is missing"
        }
    } else {
        val release = targetProps[smokeOf]
        val declared = release?.getProperty("mc_targets")?.trim()?.split(Regex("\\s+")).orEmpty()
        if (release == null || (minecraft !in declared))
            problems += "$target tests $smokeOf, which does not publish for Minecraft $minecraft"
    }
}
check(problems.isEmpty()) { "Untested or mismatched targets:\n  " + problems.joinToString("\n  ") }

stonecutter {
    create(rootProject) {
        for (target in targets) {
            val (minecraft, loader) = target.split('-')
            version(target, minecraft).buildscript("build.$loader.gradle")
        }
        // The version whose code is "uncommented" in src/ when committing.
        vcsVersion = "26.1.2-fabric"
    }
}
