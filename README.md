![Flight Affinity minecraft update style title](https://cdn.modrinth.com/data/cached_images/52ae93c4465333c7060c8094c4b170b4b539ba62.png)

## New Enchantment!

Based on the original [Aerial Affinity Mod](https://www.curseforge.com/minecraft/mc-mods/aerial-affinity), this mod adds a new enchantment which removes the speed penalty applied while mining while in the air.

This new enchantment is applied on helmets, and it can be obtained by villager trading, through the enchanting table or inside dungeons.

## Media
New Enchanted Book: **Flight Affinity**

![New Enchanted Book: Flight Affinity](https://cdn.modrinth.com/data/cached_images/aa11740a315a0fb7717b8b5b3142056f1c196f12.png)

Can Be Applied **On Any Helmet:**

![Can Be Applied On Any Helmet.](https://cdn.modrinth.com/data/cached_images/f6cb12ff87e8d0054c6e78e9b3552e9e9f426900.png)



****Without**** Flight Affinity:

[![Without-Flight-Affinity.gif](https://i.postimg.cc/W3m9FHYw/Without-Flight-Affinity.gif)](https://postimg.cc/Kkz7CJ21)

****With**** Flight Affinity:

[![WIth-Flight-Affinity.gif](https://i.postimg.cc/1zq7PtpT/WIth-Flight-Affinity.gif)](https://postimg.cc/R3vLLvpQ)

## Development

One source tree builds every supported Minecraft version and loader with [Stonecutter](https://stonecutter.kikugie.dev/).

```
src/                         shared code; version differences are //? comments
versions/<mc>-<loader>/      one folder per build target
  gradle.properties          dependency versions + mc_targets (versions the jar is published for)
build.fabric.gradle          Fabric/Quilt (Loom; remapped before 26.1)
build.neoforge.gradle        NeoForge (ModDevGradle)
build.forge.gradle           Forge 1.20.1 (ModDevGradle legacyforge)
gradle/common.gradle         shared setup, generates fabric.mod.json / mods.toml from mc_targets
gradle/publish.gradle        Modrinth + CurseForge upload, release-jar checks
```

Version-specific code uses Stonecutter comments, for example:

```java
//? if >=1.21.2 {
return registryAccess().lookupOrThrow(Registries.ENCHANTMENT)...;
//?} else {
/*return registryAccess().registryOrThrow(Registries.ENCHANTMENT)...;
*///?}
```

`fabric`, `neoforge` and `forge` work as conditions too, and classes in a `fabric`/`neoforge`/`forge`
package are only compiled for that loader.

### Everyday tasks

| What | How |
|---|---|
| Work on another version in the IDE | Run the Gradle task `stonecutter > Set active project to <target>`; run `Reset active project` before committing |
| Build one target | `./gradlew :1.21.1-neoforge:build` |
| Run the GameTests for a target | `./gradlew :1.21.1-neoforge:runGametest` |
| Check the jar players get | `./gradlew :1.21.1-neoforge:checkReleaseJar` |
| Publish one target | `MODRINTH_TOKEN=... CURSEFORGE_TOKEN=... ./gradlew :1.21.1-neoforge:publishMods` (dry run without tokens) |
| Support a new Minecraft version | Add `versions/<mc>-<loader>/gradle.properties`; CI picks it up automatically |

### Tests

`src/test` holds the GameTests. `FlightAffinityTests.ALL` is the single list of tests; each loader
registers it (see the `test/fabric`, `test/neoforge`, `test/forge` packages). When adding a test, also add
`data/flightaffinity-test/test_instance/<name>.json` (used from 1.21.5).

CI (`.github/workflows/build.yml`) runs one job per target. Each job:

1. builds the target,
2. runs its GameTests and checks that all of them passed (a test server that crashes, or registers
   no tests, does not count as a pass),
3. checks the jar players install: correct metadata file, no test classes, mixins remapped
   (Fabric) or a refmap present (Forge),
4. does a Modrinth/CurseForge publish dry run.

These GameTests run against the development build on the version each target is compiled for. They
don't yet prove that a jar works on every other Minecraft version listed in its `mc_targets`.
