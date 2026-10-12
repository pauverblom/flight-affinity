# Changelog

All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added
- Builds for Minecraft 26.2 and 26.3.

### Changed
- Minimum loader versions are now the oldest ones the mod is tested on: Fabric Loader 0.16.10 on
  Minecraft 1.20-1.20.4 and 0.15.10 on 1.20.5-1.20.6 (older ones could not load the 1.20.5-1.20.6
  version), Forge 47.1.0, and for NeoForge the first stable build of each Minecraft version.
- Quilt is only listed for the Minecraft versions it is tested on (currently all of them).
- On Minecraft 1.20.1-1.20.6 the enchantment now uses the same weight and enchanting costs as on 1.21+.

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
