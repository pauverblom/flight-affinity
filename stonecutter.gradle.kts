plugins {
    id("dev.kikugie.stonecutter")
    // Loader plugins are applied per target by build.<loader>.gradle; declaring them here
    // puts them on the build classpath once, with a single version for every target.
    id("net.fabricmc.fabric-loom") version "1.18.4" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.18.4" apply false
    id("net.neoforged.moddev") version "2.0.148" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.148" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.2.1" apply false
}

// The target loaded in the IDE. Switch with the "Set active project to ..." Gradle tasks
// and switch back to the VCS version (settings.gradle.kts) before committing.
stonecutter active "26.1.2-fabric"

stonecutter parameters {
    // Lets source files use `//? if fabric`, `//? if neoforge`, `//? if forge`.
    val loader = current.project.split('-')[1]
    constants.match(loader, "fabric", "neoforge", "forge")

    // Mojang renamed ResourceLocation to Identifier in 1.21.11. Code is written with
    // Identifier and rewritten for older targets.
    replacements.string(eval(current.version, ">=1.21.11")) {
        replace("ResourceLocation", "Identifier")
    }
}
