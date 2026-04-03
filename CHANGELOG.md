# Changelog

## [3.6.4] - 2026-01-02

### Changed
- Night skip now uses vanilla Minecraft sleep timing (100 ticks / ~5 seconds delay)
- Players experience normal sleep animation before night skips
- Improved sleep behavior to feel more natural and match vanilla gameplay

### Technical
- Modified sleep delay from 10L to 100L ticks in PlayerBedEnterEvent handler
- Removed gradual time increment system in favor of single delayed skip
- Maintains one-player requirement while preserving vanilla sleep UX
