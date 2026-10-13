# Contributing to Flight Affinity

One source tree builds every supported Minecraft version and loader. This document explains how it is
organised, how to work on it, and how it is tested and released.

## Before opening a pull request

- Run the Gradle task `stonecutter > Reset active project` if you switched versions in the IDE (CI fails otherwise).
- Follow `.editorconfig` (CI runs `python3 .github/scripts/lint.py`).
- Describe changes players will notice in `CHANGELOG.md` under `[Unreleased]`.
- CI tests every Minecraft version and loader; a pull request is ready when it is green.

## Layout

The versions are built with [Stonecutter](https://stonecutter.kikugie.dev/):

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
//? if >=1.21 {
return Identifier.fromNamespaceAndPath(namespace, path);
//?} else {
/*return new Identifier(namespace, path);
*///?}
```

`fabric`, `neoforge` and `forge` work as conditions too, and classes in a `fabric`/`neoforge`/`forge`
package are only compiled for that loader.

## Which Minecraft versions share a jar

A jar is compiled for one Minecraft version (its release target's) and published for every version in
its `mc_targets`, each of which is tested with that jar. A new jar is only needed where the compiled
code stops working on the next version:

| Loader | Jars (Minecraft versions) | Why they are separate |
|---|---|---|
| Fabric, Quilt | 1.20-1.20.4, 1.20.5-1.20.6, 1.21-1.21.8, 1.21.9-1.21.11, 26.1-26.3 | 1.20.5 changed the `Enchantment` constructor; 1.21 made enchantments data-driven; 1.21.9 changed the intermediary name of `Entity.level()`, which the mod calls; from 26.1 Minecraft is unobfuscated, so jars use Mojang's names instead of intermediary ones |
| NeoForge | 1.20.2-1.20.4, 1.20.5-1.20.6, 1.21-1.21.10, 1.21.11-26.3 | the same first two; NeoForge runs on Mojang's names, so Mojang's rename of `ResourceLocation` to `Identifier` in 1.21.11 needs another jar |
| Forge | 1.20.1 (also runs on NeoForge 1.20.1) | Forge is only built for 1.20.1 |

On Forge the mod uses Forge's `BreakSpeed` event instead of the mixin (see `FlightAffinityForge`).

Within a range, compiled code must only call what exists on every version of it. The mod looks up its
enchantment through `HolderLookup.Provider`, which kept its methods from 1.21 on (`RegistryAccess`'s
were renamed in 1.21.2), and the GameTests, compiled for each version, call the mod only for what
they test (`TestCompat.id` instead of `FlightAffinity.id`). The smoke tests catch anything else.

## Everyday tasks

| What | How |
|---|---|
| Work on another version in the IDE | Run the Gradle task `stonecutter > Set active project to <target>`; run `Reset active project` before committing |
| Build one target | `./gradlew :1.21.1-neoforge:build` |
| Run the GameTests for a target | `./gradlew :1.21.1-neoforge:runGametest` |
| Check the jar players get | `./gradlew :1.21.1-neoforge:checkReleaseJar` |
| Release | Actions tab, **release** workflow, *Run workflow* (see Releasing) |
| Support a new Minecraft version with a new jar | Add `versions/<mc>-<loader>/gradle.properties` with `mc_targets`; CI picks it up automatically |
| Publish an existing jar for one more version | Add the version to that target's `mc_targets` and add a smoke target for it (the build tells you which one is missing) |

## Tests

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
`<mc>-<loader>-<other loader>` targets run a jar on another loader it is published for (`also_runs_on`), on
each of its Minecraft versions: the Forge 1.20.1 jar on NeoForge 1.20.1, and every Fabric jar on Quilt.

Every target also runs **production tests** (`./gradlew :<target>:productionTest`, `gradle/production.gradle`).
They install a real server with the loader's own installer, on the **oldest** loader version the jar
accepts (`fabric_loader_min`, `neoforge_min`, `forge_min`; the development GameTests use the newest), and:

- start it with only the release jar (plus Fabric API on Fabric/Quilt) and check, with a console command,
  that the enchantment is registered;
- run the GameTests, packaged as a mod compiled for the server's Minecraft version, on a GameTest server. NeoForge 1.21.5-1.21.8 can only start a
  GameTest server in development, so those versions get the start-up check only.

This catches what a development environment hides: wrong metadata, a jar that was not remapped, a missing
dependency (Fabric API loads the mod's data and translations), a loader version that is too old.
The jar's declared minimum loader versions come from the same properties, so what the jar accepts is
what was tested: on Fabric `fabric_loader_min` of the release target, and on NeoForge `neoforge_min` of
the jar's oldest Minecraft version. A Fabric smoke target whose Minecraft version's Fabric API needs a
newer Fabric Loader sets its own `fabric_loader_min`, which its production tests use instead. Targets with `production_server=true` (NeoForge 20.2, 20.3 and 20.5, which ModDevGradle
cannot set up, and the Quilt targets) only run the production tests.

CI (`.github/workflows/build.yml`) runs one job per target, in two stages. A release target's job:

1. builds the target,
2. runs its GameTests and checks that all of them passed (a test server that crashes, or registers
   no tests, does not count as a pass),
3. runs the production tests,
4. checks the jar players install: correct metadata file, no test classes, mixins remapped
   (Fabric) or SRG names (Forge), translations for every language, the 1.21+ enchantment file,
5. does a Modrinth/CurseForge publish dry run,
6. uploads its jars (`exportJars`).

Then each smoke target's job downloads its release target's jars and runs steps 2 and 3 with them
(`-Pprebuilt_jars=<dir>`, see `gradle/release-jars.gradle`): the jars are built once, and the files the smoke
tests check are the ones the release workflow publishes. Locally, smoke targets build the release target
themselves. A newer push cancels the CI run of an older one; failed downloads are retried with backoff.

## Releasing

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

Each upload's changelog starts with its Minecraft versions and loaders. Only one jar per release (the
one for the newest Minecraft version) carries the changes from `CHANGELOG.md`, since Modrinth's
changelog page lists every jar; the others link to the GitHub release. A jar that only widens one
already published says that the mod itself did not change.

If an upload fails half-way, run the workflow again: it continues with what is missing. (Modrinth decides
what exists; if only the CurseForge upload of a jar failed, upload that file to CurseForge by hand.)

## New Minecraft versions

Every Monday the **new-minecraft-versions** workflow (`.github/scripts/new_minecraft_versions.py`; it can
also be started by hand) looks for Minecraft releases that no jar covers. For each one, as soon as
Fabric API or NeoForge has builds for it, it does what you would do by hand: adds the version to the
`mc_targets` of the newest jar before it, creates its smoke target, and runs the GameTests and
production tests. Then it:

- pushes the additions that passed to the branch `compat/new-minecraft-versions` (rewritten each week,
  so don't add commits to it) and starts CI on it;
- opens an issue *Minecraft &lt;version&gt;: add support*, assigned to the repository owner, saying per loader
  whether the existing jar works, which builds are still missing, or what failed (then a new target
  with code changes is needed). The issue is updated, with a comment, when that changes.

GitHub emails you about issues assigned to you and comments that mention you, as long as email is
enabled under Settings > Notifications ("Participating, @mentions and custom"). To add the version,
open a pull request from the branch (the issue links it), merge it, and run the release workflow:
only the jars whose range grew are uploaded. Close the issue to stop updates for a version.

## Download statistics

The README's download chart and Minecraft version badge come from the **download-stats** workflow
(`.github/scripts/download_stats.py`). Once a day it records the Modrinth and CurseForge download totals
in `downloads.csv` and redraws `downloads.svg` and `minecraft.json`, all on the `stats` branch. Run it by
hand with *backfill* ticked to fill in Modrinth's past days; this needs `MODRINTH_TOKEN` (release
environment) to have the analytics scope. CurseForge has no history to fetch: its line starts on the
first recorded day.

## Branches

Everything is developed on the default branch. The repository used to have one branch per group of
Minecraft versions; their last state is kept as the tags `legacy/<branch>` (for example
`legacy/1.20-1.20.4`).
