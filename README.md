**Version:** 4.0.1 (8 May 2026) | **Author:** LunoX2 | **License:** MIT
![Why is One Player Sleep](https://cdn.modrinth.com/data/cached_images/8788d344a42533aae1197a6c998a757a6b7876b9.png)

# 50K Downloads on Modrinth https://cdn.discordapp.com/emojis/1222721374079156264.webp?size=64

A lightweight, optimized Minecraft plugin that allows a single player to skip the night by sleeping, without requiring all online players to sleep. Ideal for servers where players are spread across different areas or time zones.

---

## Overview

OnePlayerSleep replaces Minecraft's default sleep requirement with a single-player trigger. When any player sleeps, the night is skipped for the entire server. The plugin is fully configurable, permission-aware, and built for minimal performance impact.

---

## Features

- Single player can skip night for the entire server
- Optional weather clearing when night is skipped
- Fully customizable plugin name, messages, and colors
- Simple enable, disable, and reload commands
- LuckPerms-compatible permission system
- Toggle broadcasts, command messages, and console logging independently
- Message and config caching for minimal overhead
- Supports Minecraft 1.16 through 1.21+

---

## Installation

1. Download `OnePlayerSleep-xx.jar`
2. Place the JAR file in your server's `plugins/` directory
3. Restart the server (or use `/reload confirm` at your own risk)
4. The configuration file will be auto-generated at `plugins/OnePlayerSleep/config.yml`
5. Customize settings as needed and reload with `/oneplayersleep reload`

---

## Configuration

### Default `config.yml`

```yaml
# OnePlayerSleep Configuration
# Customize your plugin settings here

# Plugin Display Name
# Change this to match your server name (e.g., GlaceSleep, DreamSleep, etc.)
plugin-name: "OnePlayerSleep"

# Enable/Disable plugin on startup
enabled-on-startup: true

# Weather Clearing
# Set to true to clear rain/thunder when skipping night
# Set to false to only skip time without affecting weather
weather-clear: false

# Message Settings
message-settings:
  # Show message when night is skipped
  show-night-skip: true
  # Show messages for enable/disable commands
  show-toggle-messages: true
  # Send messages to console
  console-logging: true

# Messages (Use & for color codes)
messages:
  night-skipped: "&6Night skipped by %player%"
  plugin-enabled: "&a%plugin% enabled!"
  plugin-disabled: "&c%plugin% disabled!"
  plugin-reloaded: "&a%plugin% configuration reloaded!"
  no-permission: "&cYou don't have permission to use this command."
  status-enabled: "&6%plugin% is currently &aenabled&6."
  status-disabled: "&6%plugin% is currently &cdisabled&6."
  usage: "&6Usage: /oneplayersleep <enable|disable|reload>"

# Do not modify this
config-version: 1
```

---

## Commands

| Command | Description | Permission |
|---|---|---|
| `/oneplayersleep` | Show plugin status and usage | `oneplayersleep.toggle` |
| `/oneplayersleep enable` | Enable the plugin | `oneplayersleep.toggle` |
| `/oneplayersleep disable` | Disable the plugin | `oneplayersleep.toggle` |
| `/oneplayersleep reload` | Reload configuration | `oneplayersleep.reload` |

---

## Permissions

| Permission | Description | Default |
|---|---|---|
| `oneplayersleep.toggle` | Allows toggling the plugin on/off | OP |
| `oneplayersleep.reload` | Allows reloading the configuration | OP |
| `oneplayersleep.*` | Grants all permissions | OP |

### LuckPerms Setup

Grant a permission to a specific player:

```
/lp user <username> permission set oneplayersleep.toggle true
```

Grant a permission to a group:

```
/lp group <groupname> permission set oneplayersleep.toggle true
```

Grant all permissions to a player:

```
/lp user <username> permission set oneplayersleep.* true
```

---

## Compatibility

### Minecraft Versions

- 1.16.x through 1.21.x — Fully supported
- Future versions — Expected to remain compatible

### Server Software

- Spigot
- Paper (Recommended)
- Purpur
- Any Spigot-based server

### Java Versions

- Java 17 (Minimum required)
- Java 21 LTS (Recommended)
- Java 22+

---

## Performance

The plugin is designed to have a negligible impact on server performance.

| Metric | Value |
|---|---|
| Memory usage | ~1 MB |
| Startup time | < 100 ms |
| Per-event processing | < 1 ms |

Optimizations include config value caching, message pre-processing, reflection method caching, and efficient event handling.

---

## Frequently Asked Questions

**Does this work with sleeping percentage plugins?**
OnePlayerSleep bypasses the vanilla sleep percentage requirement entirely. It may conflict with other sleep plugins.

**Can I require 2 players to sleep instead of 1?**
No. This plugin is designed specifically for single-player sleep triggering.

**Does it work in the Nether or End?**
No. Sleeping only functions in the Overworld, as per Minecraft's core mechanics.

**Will it work on Minecraft 1.12 or older?**
No. The minimum supported version is 1.16 due to API requirements.

**Can I disable the startup messages?**
Yes. Set `message-settings.console-logging: false` in your config.

**Does weather clearing affect thunderstorms?**
Yes. When `weather-clear: true`, both rain and thunder are cleared when night is skipped.
