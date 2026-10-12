![Flight Affinity minecraft update style title](https://cdn.modrinth.com/data/cached_images/52ae93c4465333c7060c8094c4b170b4b539ba62.png)

## New Enchantment!

Based on the original [Aerial Affinity Mod](https://www.curseforge.com/minecraft/mc-mods/aerial-affinity), this mod adds a new enchantment which removes the speed penalty applied while mining while in the air.

This new enchantment is applied on helmets, and it can be obtained by villager trading, through the enchanting table or inside dungeons.

On Fabric and Quilt, Flight Affinity requires [Fabric API](https://modrinth.com/mod/fabric-api).

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
versions/<mc>-<loader>/      one folder per target
  gradle.properties          dependency versions + mc_targets (versions the jar is published for),
                             or smoke_of=<target> for a smoke test (see Tests)
build.fabric.gradle          Fabric/Quilt (Loom; remapped before 26.1)
build.neoforge.gradle        NeoForge (ModDevGradle)
build.forge.gradle           Forge 1.20.1 (ModDevGradle legacyforge)
gradle/common.gradle         shared setup, generates fabric.mod.json / mods.toml from mc_targets
gradle/publish.gradle        Modrinth + CurseForge upload, release-jar checks
gradle/production.gradle     production tests on servers installed by each loader's installer (see Tests)
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

### Jar names

Every jar is named `flightaffinity-<loader>-<mod version>+mc<first>-<last>.jar` (or `+mc<version>` when it
supports a single Minecraft version). The range is inclusive: `flightaffinity-neoforge-1.3.0+mc1.20.2-1.20.4.jar`
works on 1.20.2, 1.20.3 and 1.20.4. The Forge 1.20.1 jar also runs on NeoForge 1.20.1.

### Everyday tasks

| What | How |
|---|---|
| Work on another version in the IDE | Run the Gradle task `stonecutter > Set active project to <target>`; run `Reset active project` before committing |
| Build one target | `./gradlew :1.21.1-neoforge:build` |
| Run the GameTests for a target | `./gradlew :1.21.1-neoforge:runGametest` |
| Check the jar players get | `./gradlew :1.21.1-neoforge:checkReleaseJar` |
| Release | Actions tab, **release** workflow, *Run workflow* (see Releasing) |
| Support a new Minecraft version with a new jar | Add `versions/<mc>-<loader>/gradle.properties` with `mc_targets`; CI picks it up automatically |
| Publish an existing jar for one more version | Add the version to that target's `mc_targets` and add a smoke target for it (the build tells you which one is missing) |

### Tests

`src/test` holds the GameTests. `FlightAffinityTests.ALL` is the single list of tests; each loader
registers it (see the `test/fabric`, `test/neoforge`, `test/forge` packages). When adding a test, also add
`data/flightaffinity-test/test_instance/<name>.json` (used from 1.21.5).

Every published Minecraft version is tested, not just the one a jar is compiled for. Two kinds of
target make that happen:

- A **release target** (`mc_targets=...`) builds a jar and runs the GameTests on the Minecraft version
  it is compiled for.
- A **smoke target** (`smoke_of=<release target>`) runs the same GameTests on another Minecraft
  version, with the release target's jar installed in the `mods` folder exactly as a player installs it.
  Only the tests are compiled; the mod itself comes from the jar. For example, `1.20.2-neoforge` tests
  `flightaffinity-neoforge-1.3.0+mc1.20.2-1.20.4.jar` on Minecraft 1.20.2.

`settings.gradle.kts` refuses to build if any version in an `mc_targets` list, or any loader in
`also_runs_on`, has no smoke target, so a jar cannot be published for a version or loader nobody tested.
`<mc>-<loader>-<other loader>` targets run a jar on another loader: the Forge 1.20.1 jar on NeoForge 1.20.1,
and each Fabric jar on Quilt.

Every target also runs **production tests** (`./gradlew :<target>:productionTest`, `gradle/production.gradle`).
They install a real server with the loader's own installer, on the **oldest** loader version the jar
accepts (`fabric_loader_min`, `neoforge_min`, `forge_min`; the development GameTests use the newest), and:

- start it with only the release jar (plus Fabric API on Fabric/Quilt) and check, with a console command,
  that the enchantment is registered;
- run the GameTests, packaged as a mod, on a GameTest server. NeoForge 1.21.5-1.21.8 can only start a
  GameTest server in development, so those versions get the start-up check only.

This catches what a development environment hides: wrong metadata, a jar that was not remapped, a missing
dependency (Fabric API loads the mod's data and translations), a loader version that is too old.
The jar's declared minimum loader versions come from the same properties, so what the jar accepts is
what was tested. Targets with `production_server=true` (NeoForge 20.2, 20.3 and 20.5, which ModDevGradle
cannot set up, and the Quilt targets) only run the production tests.

CI (`.github/workflows/build.yml`) runs one job per target. A release target's job:

1. builds the target,
2. runs its GameTests and checks that all of them passed (a test server that crashes, or registers
   no tests, does not count as a pass),
3. runs the production tests,
4. checks the jar players install: correct metadata file, no test classes, mixins remapped
   (Fabric) or a refmap present (Forge), translations for every language, the 1.21+ enchantment file,
5. does a Modrinth/CurseForge publish dry run.

A smoke target's job runs steps 2 and 3 with the release jar.

### Releasing

Releases are published by the **release** workflow (`.github/workflows/release.yml`), started by hand
from the Actions tab. Leave *publish* unticked for a dry run that lists what would be uploaded. It:

1. refuses to run if `CHANGELOG.md` has unreleased changes or no section for `mod_version`,
2. runs every test (the whole CI matrix),
3. uploads each jar that is not on Modrinth yet to Modrinth and CurseForge, and attaches it to the
   GitHub release `v<mod_version>` (created on first use).

The tokens are the `MODRINTH_TOKEN` and `CURSEFORGE_TOKEN` secrets of the `release` environment
(Settings > Environments); add yourself as a required reviewer there to approve each release.

Each jar is one upload, named by its mod version, Minecraft range and loader, e.g.
`1.3.0+mc26.3-26.3.1-fabric`. Uploads that already exist are skipped, so **what gets published follows
from what changed**:

| Change | What to do | Published |
|---|---|---|
| The mod changed (fix, feature, balance) | Bump `mod_version`, describe it in `CHANGELOG.md` | Every jar |
| A new Minecraft version works with an existing jar | Add it to that target's `mc_targets` and add its smoke target | That jar only, rebuilt for the wider range (the range is part of its metadata), same mod version |
| A new Minecraft version needs code changes | Add a target (version-specific code behind `//?` conditions) | The new jars only, same mod version |

The mod version only changes when the mod does: `+mc...` is build metadata, so
`1.3.0+mc26.3` and `1.3.0+mc26.3-26.3.1` are the same release of the mod for different Minecraft
ranges. The older upload stays available for the versions it covered. A change to shared code that
alters existing jars needs a new `mod_version`, since those jars would otherwise be republished under
an unchanged version.

If an upload fails half-way, run the workflow again: it continues with what is missing. (Modrinth decides
what exists; if only the CurseForge upload of a jar failed, upload that file to CurseForge by hand.)
