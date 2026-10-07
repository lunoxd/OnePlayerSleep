# About OnePlayerSleep

> Comprehensive technical analysis, architectural overview, and future improvement roadmap for **OnePlayerSleep**.

---

## 📌 Project Overview

**OnePlayerSleep** is an ultra-lightweight, high-performance Minecraft server plugin designed for Spigot, Paper, Purpur, and Folia (1.16 – 1.21.x & 26.x). It enables a single player sleeping in a bed to advance the world time to day and optionally clear bad weather without requiring all online players to sleep simultaneously.

- **Current Version:** `4.1.0`
- **Supported Platforms:** Paper, Purpur, Folia, Spigot (API 1.16+)
- **Target Java Version:** Java 17+ (Java 21 LTS recommended)
- **Primary Package:** `com.ops`
- **Author:** heyWaffie / LunoX2
- **License:** MIT

---

## 🏗️ Architecture & Component Breakdown

```
ops/
├── pom.xml                               # Maven build definition & shade configuration
├── README.md                             # User-facing summary & presentation
├── DOCUMENTATION.md                      # Detailed user & administrator guide
├── CHANGELOG.md                          # Release history
├── about.md                              # Technical analysis & architecture documentation
├── src/
│   └── main/
│       ├── java/
│       │   └── com/ops/
│       │       ├── OnePlayerSleep.java   # Main plugin class, listener, commands & tab completer
│       │       └── SchedulerAdapter.java # Multi-threaded Folia & Bukkit/Paper scheduler bridge
│       └── resources/
│           ├── plugin.yml                # Plugin manifest, command & permissions definitions
│           └── config.yml                # Configurable messages, options & toggles
```

### 1. Scheduler & Concurrency Model (Folia & Paper Dual Support)
The plugin dynamically detects whether it is running on a Folia-based multithreaded server or standard Paper/Spigot via [`SchedulerAdapter.java`](file:///Users/luno/ops/src/main/java/com/ops/SchedulerAdapter.java):
- **Global Region Operations (`runGlobal`):** Interacts with `Bukkit.getGlobalRegionScheduler().run(Plugin, Consumer<ScheduledTask>)`. Global world modifications (`w.setTime(0)`, weather changes) execute safely on Folia's global thread. On Paper/Spigot, it executes on the primary server thread.
- **Entity/Player Schedulers (`runDelayedForPlayer` & `runForPlayer`):** Uses `Player.getScheduler().runDelayed(Plugin, Consumer<ScheduledTask>, Runnable, long)` and `run(Plugin, Consumer<ScheduledTask>, Runnable)`. Player-specific delays (e.g., the 100-tick sleep timer) and `player.wakeup(false)` actions execute safely on the player's region thread in Folia.

### 2. Bed Entry & Night Skip Lifecycle
1. **Event Interception:** Listens to `PlayerBedEnterEvent` (`EventPriority.NORMAL`, `ignoreCancelled = true`).
2. **Validation:** Checks if plugin is enabled and `BedEnterResult == OK`.
3. **Overworld & Night/Storm Check:** Verifies the world environment is `NORMAL` and either world time is between `12541` and `23458` or the world has active thunderstorm (`w.isThundering()`).
4. **Natural Sleep Delay:** Schedules a 100-tick (~5.0s) delayed task on the sleeping player's scheduler to let the vanilla bed sleep animation play out.
5. **Execution:** If the player is still online, in bed, and the condition still holds:
   - Resets world time to `0` (Day) via global region scheduler.
   - Clears weather (`w.setStorm(false)`, `w.setThundering(false)`) if `weather-clear: true`.
   - Safely calls `wakeup(false)` on all sleeping players on their respective entity schedulers.
   - Broadcasts the night skip notification to all players in that world.

### 3. Configuration & Messages
- `config.yml` provides customizable display names, feature switches (`weather-clear`, `enabled-on-startup`, `message-settings`), and formatted messages with `%player%` and `%plugin%` tokens.
- Color codes are parsed using standard `&` formatting codes.

### 4. Commands, Tab Completion & Permissions
- `/oneplayersleep` — Displays status (including message and weather clear status) and usage.
- `/oneplayersleep enable` — Enables plugin functionality.
- `/oneplayersleep disable` — Disables night skipping.
- `/oneplayersleep msg <on|off>` — Toggles night skip broadcast chat messages dynamically and persists to config.
- `/oneplayersleep weather <on|off>` — Toggles weather clearing when skipping night and persists to config.
- `/oneplayersleep reload` — Reloads `config.yml` from disk.
- **Tab Completion:** Auto-completes all subcommands (`enable`, `disable`, `msg [on|off]`, `weather [on|off]`, `reload`) dynamically based on sender permissions.
- **Permissions:** `oneplayersleep.toggle`, `oneplayersleep.reload`, `oneplayersleep.*`.

---

## 🔍 Codebase Audit & Technical Notes

| Area | Current Implementation | Observations / Technical Notes |
| :--- | :--- | :--- |
| **Folia Concurrency** | Dedicated [`SchedulerAdapter.java`](file:///Users/luno/ops/src/main/java/com/ops/SchedulerAdapter.java) | Resolved prior `NoSuchMethodException` and async world modification errors by using correct `Consumer<ScheduledTask>` reflection signatures for `GlobalRegionScheduler` and `EntityScheduler`. |
| **Daytime Thunderstorms** | `isNightOrStormInOverworld()` | Supports skipping thunderstorm weather during daytime (`w.isThundering()`). |
| **Tab Completion** | Implemented in [OnePlayerSleep.java](file:///Users/luno/ops/src/main/java/com/ops/OnePlayerSleep.java) | Permission-aware tab auto-completion for all subcommands. |
| **Version Compatibility** | 1.16 through 1.21.x & 26.x | Compatible with all current and upcoming Spigot/Paper/Folia releases. |
| **Formatting Engine** | Legacy `&` -> `§` translation | Modern Paper servers can optionally benefit from MiniMessage / Adventure components for HEX gradients. |

---

## 🚀 Improvement Roadmap & Planned Enhancements

### 🟢 Phase 1: Code Architecture & QoL (Completed & In Progress)
- [x] **Folia Schedulers Fix:** Correct Folia reflection with `GlobalRegionScheduler` and `EntityScheduler`.
- [x] **Tab Completion:** Add tab-completion for `/oneplayersleep [enable|disable|reload]`.
- [x] **Daytime Thunderstorm Support:** Allow skipping when `world.isThundering()`.
- [x] **Documentation & Version Harmonization:** Updated docs across the repo to 4.1.0.

### 🟡 Phase 2: Enhanced Gameplay & Audio-Visual Feedback
- [ ] **Sound & Title / Actionbar Broadcasts:**
  - Configurable notification display modes: `CHAT`, `ACTION_BAR`, `TITLE`, or `BOSS_BAR`.
  - Configurable sound playback when night skips (e.g. `ENTITY_PLAYER_LEVELUP`, `UI_TOAST_CHALLENGE_COMPLETE`).
- [ ] **Smooth Night Skip Animation (Optional):**
  - Add an option in `config.yml` for smooth fast-forwarding time (accelerating time tick-by-tick) vs instant time jump (`setTime(0)`).
- [ ] **Wake-up Buffs & Insomnia Reset:**
  - Reset player insomnia / phantom counters upon successful sleep.
  - Optional morning potion buffs (e.g., Regeneration or Speed for a few seconds).

### 🔵 Phase 3: Advanced Server Administration & Integrations
- [ ] **Multi-World Filtering:**
  - World whitelist / blacklist in `config.yml`.
  - Per-world custom messages or toggle states.
- [ ] **Vanish & AFK Support:**
  - Integrations/checks with EssentialsX, CMI, SuperVanish, PremiumVanish to ignore AFK or vanished players from sleep calculations (if percentage sleep mode is enabled).
- [ ] **Flexible Sleep Modes:**
  - Configurable sleep modes: `SINGLE_PLAYER`, `PERCENTAGE` (e.g., 20% of active players), or `COUNT` (e.g., fixed minimum number of players).
- [ ] **MiniMessage & Modern Adventure Formatting:**
  - Full RGB / Hex color and gradient support via Kyori Adventure / MiniMessage with fallback for legacy codes.
- [ ] **bStats Metrics Integration:**
  - Anonymous usage telemetry for tracking active server versions and player adoption.

---

## 🛠️ Build & Development Quickstart

### Prerequisites
- JDK 17 or higher
- Maven 3.8+

### Commands
```bash
# Compile and build shaded JAR
mvn clean package

# Output artifact location:
# builds/OnePlayerSleep-4.1.0.jar
```
