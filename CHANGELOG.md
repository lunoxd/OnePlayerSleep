# Changelog

## [4.1.0] - 2026-10-07

### Added
- **Message Broadcast Toggle:** Added `/oneplayersleep msg <on|off>` command to dynamically toggle night-skip broadcast messages in chat with auto-saving to config.
- **In-Config Guide & LuckPerms Cheatsheet:** Added built-in documentation in `config.yml` detailing permissions, LuckPerms setup, commands, color codes, and placeholders.
- **Folia Multithreading Fix:** Completely rewritten Folia scheduler integration using `GlobalRegionScheduler` for world modifications and `EntityScheduler` for player actions. Fixed NoSuchMethodException and thread safety exceptions on Folia.
- **Modern Minecraft Version Compatibility:** Full support verified for Minecraft 1.16 through 1.21.x and 26.x (Wilderness Bound / game drops series).
- **Daytime Thunderstorm Support:** Players sleeping during daytime thunderstorms now properly skip the storm and advance time.
- **Configurable Sleep Delay & Disabled Worlds:** Added `sleep-delay-ticks` and `disabled-worlds` list in `config.yml`.
- **Tab Completion:** Added dynamic tab-completer for `/oneplayersleep <enable|disable|msg [on|off]|reload>` with permission awareness.
- **Dynamic Startup Logging:** Console banner displays detected server platform (Folia vs Paper/Spigot) and dynamic version number.

## [4.0.1] - 2026-01-02

### Changed
- Night skip now uses vanilla Minecraft sleep timing (100 ticks / ~5 seconds delay)
- Players experience normal sleep animation before night skips
- Improved sleep behavior to feel more natural and match vanilla gameplay

### Technical
- Modified sleep delay from 10L to 100L ticks in PlayerBedEnterEvent handler
- Removed gradual time increment system in favor of single delayed skip
- Maintains one-player requirement while preserving vanilla sleep UX
