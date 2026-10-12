# Changelog

All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [1.4.0] - 2026-10-12

### Added
- Support for Minecraft 26.2 and 26.3.
- A description of the enchantment for tooltip mods such as Enchantment Descriptions.

### Changed
- Fewer files to choose from: on Fabric and Quilt one file covers Minecraft 1.21-1.21.8, one
  1.21.9-1.21.11 and one 26.1-26.3; on NeoForge one covers 1.21-1.21.10 and one 1.21.11-26.3.
- Minimum loader versions are now the oldest ones the mod is tested on: Fabric Loader 0.16.10 on
  Minecraft 1.20-1.20.4, 0.15.10 on 1.20.5-1.20.6 (older ones could not load the mod), 0.15.11 on
  1.21-1.21.8, 0.17.0 on 1.21.9-1.21.11 and 0.18.4 on 26.1-26.3; Forge 47.1.0; and the first stable
  NeoForge build of the oldest Minecraft version of each file.
- Quilt is only listed for the Minecraft versions it is tested on (currently all of them).
- On Minecraft 1.20-1.20.6 the enchantment now uses the same weight and enchanting costs as on 1.21+.
- On Minecraft 1.21+ the enchantment can now also appear on mobs' armour and on enchanted armour
  sold by villagers, as it does on 1.20.
- On Forge the mod no longer uses a mixin, so it cannot conflict with other mods that change the
  same mining speed calculation.

### Fixed
- The mod now refuses to load if its mixin cannot apply, instead of silently doing nothing.

## [1.3.0] - 2026-04-02

### Added
- NeoForge support — Flight Affinity is now a multiloader mod available for both Fabric/Quilt and NeoForge.

### Changed
- Updated to Minecraft 26.1.
- Simplified internal mixin logic.

## [1.2.0]

### Changed
- Restructured the codebase.

## [1.0.3]

### Added
- Added mod icon for ModMenu.

### Fixed
- Fixed incompatibility with Artifacts.
- Cleaned up code.

## [1.0.0]

### Added
- Introduced Flight Affinity for Fabric 1.20.1.
