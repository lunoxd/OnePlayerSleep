# OnePlayerSleep Documentation

## 📖 Overview

**OnePlayerSleep** is a lightweight, optimized Minecraft plugin that allows a single player to skip the night by sleeping, instead of requiring all players to sleep. It uses runtime scheduler detection so it stays simple on Paper and safe on Folia.

**Version:** 4.0.1  
**Author:** heyWaffie  
**License:** Not specified

---

## ✨ Features

- 🌙 **One Player Sleep** - Single player can skip night for everyone
- ⚡ **Optional Weather Clearing** - Choose whether to clear rain/thunder when skipping night
- 🎨 **Fully Customizable** - Change plugin name, messages, and colors
- 🔧 **Simple Commands** - Enable, disable, reload configuration
- 🔐 **Permission System** - LuckPerms compatible with granular permissions
- 📊 **Message Controls** - Toggle broadcasts, command messages, console logs
- ✨ **Aesthetic UI** - Beautiful colored console messages on startup/shutdown
- 🚀 **Optimized Performance** - Message and config caching for minimal overhead
- 🌐 **Multi-Version Support** - Paper 1.16+ and Folia 1.21+

---

## 📦 Installation

1. Download `OnePlayerSleep-3.6.2.jar`
2. Place the JAR file in your server's `plugins/` folder
3. Restart your server (or use `/reload confirm` at your own risk)
4. Configuration file will be auto-generated at `plugins/OnePlayerSleep/config.yml`
5. Customize settings and reload with `/oneplayersleep reload`

---

## ⚙️ Configuration

### Default config.yml

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

### Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `plugin-name` | String | "OnePlayerSleep" | Customizable plugin display name |
| `enabled-on-startup` | Boolean | true | Whether plugin starts enabled |
| `weather-clear` | Boolean | false | Clear rain/thunder when skipping night |
| `show-night-skip` | Boolean | true | Show broadcast when night is skipped |
| `show-toggle-messages` | Boolean | true | Show enable/disable command messages |
| `console-logging` | Boolean | true | Show startup/shutdown messages in console |

### Message Placeholders

- `%player%` - Name of the player who slept
- `%plugin%` - Custom plugin name from config

### Color Codes

Use `&` followed by a color code:
- `&0` - Black
- `&1` - Dark Blue
- `&2` - Dark Green
- `&3` - Dark Aqua
- `&4` - Dark Red
- `&5` - Dark Purple
- `&6` - Gold
- `&7` - Gray
- `&8` - Dark Gray
- `&9` - Blue
- `&a` - Green
- `&b` - Aqua
- `&c` - Red
- `&d` - Light Purple
- `&e` - Yellow
- `&f` - White
- `&l` - Bold
- `&o` - Italic
- `&n` - Underline
- `&r` - Reset

---

## 🎮 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/oneplayersleep` | Show plugin status and usage | `oneplayersleep.toggle` |
| `/oneplayersleep enable` | Enable the plugin | `oneplayersleep.toggle` |
| `/oneplayersleep disable` | Disable the plugin | `oneplayersleep.toggle` |
| `/oneplayersleep reload` | Reload configuration | `oneplayersleep.reload` |

---

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `oneplayersleep.toggle` | Allows toggling plugin on/off | OP |
| `oneplayersleep.reload` | Allows reloading configuration | OP |
| `oneplayersleep.*` | Grants all permissions | OP |

### LuckPerms Setup

Grant permission to a player:
```
/lp user <username> permission set oneplayersleep.toggle true
```

Grant permission to a group:
```
/lp group <groupname> permission set oneplayersleep.toggle true
```

Grant all permissions:
```
/lp user <username> permission set oneplayersleep.* true
```

---

## 🌍 Compatibility

### Minecraft Versions
- ✅ **1.16.x** - Fully supported
- ✅ **1.17.x** - Fully supported
- ✅ **1.18.x** - Fully supported
- ✅ **1.19.x** - Fully supported
- ✅ **1.20.x** - Fully supported
- ✅ **1.21.x** - Fully supported
- ✅ **Future versions** - Should remain compatible

### Server Software
- ✅ Spigot
- ✅ Paper (Recommended)
- ✅ Purpur
- ✅ Any Spigot-based server

### Java Versions
- ✅ Java 17 (Minimum required)
- ✅ Java 18
- ✅ Java 19
- ✅ Java 20
- ✅ Java 21 (LTS - Recommended)
- ✅ Java 22+

---

## 💡 Usage Examples

### Example 1: Customizing for Your Server

If your server is called "GlaceNetwork", customize the plugin:

```yaml
plugin-name: "GlaceSleep"

messages:
  night-skipped: "&b&lGlace &7» &fNight skipped by &e%player%"
  plugin-enabled: "&a&l✓ &fGlaceSleep is now enabled!"
  plugin-disabled: "&c&l✗ &fGlaceSleep is now disabled!"
```

### Example 2: Silent Mode

Disable all messages except night skip:

```yaml
message-settings:
  show-night-skip: true
  show-toggle-messages: false
  console-logging: false
```

### Example 3: Enable Weather Clearing

Make the plugin also clear rain/thunder:

```yaml
weather-clear: true
```

### Example 4: Minimal Setup

Bare minimum configuration:

```yaml
plugin-name: "Sleep"
enabled-on-startup: true
weather-clear: false
message-settings:
  show-night-skip: true
  show-toggle-messages: false
  console-logging: false
messages:
  night-skipped: "&7Night skipped"
```

---

## 🔧 Troubleshooting

### Plugin not working

1. **Check server version** - Must be 1.16+ with Java 17+
2. **Check permissions** - Players need `oneplayersleep.toggle` (OPs have it by default)
3. **Check if enabled** - Run `/oneplayersleep` to see status
4. **Check console** - Look for error messages on startup
5. **Reload config** - Run `/oneplayersleep reload` after changes

### Weather still clearing when disabled

- Ensure `weather-clear: false` in config.yml
- Run `/oneplayersleep reload` to apply changes
- Check for conflicting plugins

### Commands not working

- Verify permission `oneplayersleep.toggle` is granted
- Check if plugin is loaded: `/plugins` (should be green)
- Try restarting the server

### Messages not showing

- Check `message-settings.show-night-skip` is `true`
- Verify color codes use `&` not `§`
- Ensure messages aren't empty strings

---

## 🎨 Customization Ideas

### Themed Servers

**Skyblock Server:**
```yaml
plugin-name: "SkyblockSleep"
messages:
  night-skipped: "&b☁ &fIsland night skipped by &e%player%"
```

**RPG Server:**
```yaml
plugin-name: "DreamRealm"
messages:
  night-skipped: "&5⚔ &dThe realm dreams as &e%player% &drests"
```

**Survival Server:**
```yaml
plugin-name: "QuickRest"
messages:
  night-skipped: "&2🌲 &a%player% &7has allowed everyone to rest"
```

---

## 📊 Performance

The plugin is highly optimized:
- **Memory:** ~1MB RAM usage
- **CPU:** Minimal overhead (event-driven, cached config)
- **Startup:** <100ms load time
- **Per-event:** <1ms processing time

Optimizations include:
- Config value caching
- Message pre-processing
- Reflection method caching
- Efficient event handling

---

## 🛠️ Developer Information

### Building from Source

```bash
git clone <repository-url>
cd mcpluggins
mvn clean package
```

JAR will be generated at `builds/OnePlayerSleep-3.6.2.jar`

### Project Structure

```
mcpluggins/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/ops/OnePlayerSleep.java
│       └── resources/
│           ├── config.yml
│           └── plugin.yml
├── builds/
│   └── OnePlayerSleep-3.6.2.jar
├── pom.xml
└── README.md
```

---

## 📝 Changelog

### Version 3.6.2 (Current)
- Renamed `wc` to `weather-clear` for clarity
- Improved documentation
- Updated console messages

### Version 3.2.6
- Added optional weather clearing feature
- Weather clearing now disabled by default
- Config option: `weather-clear: false`

### Version 3.2
- Added reload command
- Optimized performance with caching
- Added message visibility controls
- Improved multi-version compatibility

---

## ❓ FAQ

**Q: Does this work with sleeping percentage plugins?**  
A: This plugin bypasses the vanilla sleep percentage requirement entirely. It may conflict with other sleep plugins.

**Q: Can I make it require 2 players instead of 1?**  
A: Not currently. This plugin is designed specifically for single-player sleep.

**Q: Does it work in the Nether or End?**  
A: No, sleeping only works in the Overworld (normal world) as per Minecraft mechanics.

**Q: Will it work on older versions like 1.12?**  
A: No, minimum version is 1.16 due to API requirements.

**Q: Can I disable the startup messages?**  
A: Yes, set `message-settings.console-logging: false` in config.

**Q: Does weather clearing affect thunderstorms?**  
A: Yes, when `weather-clear: true`, both rain and thunder are cleared.

---

## 📞 Support

For issues, suggestions, or questions:
- Check this documentation first
- Review troubleshooting section
- Verify configuration syntax
- Check server console for errors

---

## 📜 License

Not specified. Please contact the author for licensing information.

---

**Last Updated:** January 1, 2026  
**Documentation Version:** 1.0  
**Plugin Version:** 4.0.1
