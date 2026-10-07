package com.ops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnePlayerSleep extends JavaPlugin implements Listener, TabCompleter {
    private SchedulerAdapter schedulerAdapter;

    private boolean enabled = true;
    private String pluginName;
    private long sleepDelayTicks = 100L;
    private boolean weatherClearing;
    private Set<String> disabledWorlds = new HashSet<>();

    private boolean showNightSkip;
    private boolean showToggleMsgs;
    private boolean consoleLogging;
    private String nightSkipMsg;
    private String enabledMsg;
    private String disabledMsg;
    private String msgEnabledMsg;
    private String msgDisabledMsg;
    private String weatherEnabledMsg;
    private String weatherDisabledMsg;
    private String reloadedMsg;
    private String noPermMsg;
    private String statusEnabledMsg;
    private String statusDisabledMsg;
    private String usageMsg;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        this.schedulerAdapter = new SchedulerAdapter(this);

        getServer().getPluginManager().registerEvents(this, this);

        PluginCommand cmd = getCommand("oneplayersleep");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }

        if (consoleLogging) {
            String version = getDescription().getVersion();
            String serverVer = Bukkit.getVersion();
            if (serverVer.length() > 22) {
                serverVer = serverVer.substring(0, 22);
            }
            String platform = schedulerAdapter.isFolia() ? "Folia (Multithreaded)" : "Paper / Spigot / Bukkit";

            log("");
            log("§b[==================================================]");
            log(String.format("§b[  %-15s v%-6s                      ]", pluginName, version));
            log("§b[  Author: heyWaffie / LunoX2                      ]");
            log(String.format("§b[  Platform: %-25s             ]", platform));
            log(String.format("§b[  Server:   %-25s             ]", serverVer));
            log("§b[--------------------------------------------------]");
            log("§b[  [+] Event Listeners   -> LOADED                 ]");
            log("§b[  [+] Commands & Tab    -> REGISTERED             ]");
            log("§b[  [+] Scheduler Adapter -> INITIALIZED            ]");
            log("§b[--------------------------------------------------]");
            log("§b[  [*] One Player Sleep  -> READY                  ]");
            log("§b[==================================================]");
            log("");
        }
    }

    private void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    private void loadConfig() {
        pluginName = getConfig().getString("plugin-name", "OnePlayerSleep");
        enabled = getConfig().getBoolean("enabled-on-startup", true);
        sleepDelayTicks = Math.max(0L, getConfig().getLong("sleep-delay-ticks", 100L));
        weatherClearing = getConfig().getBoolean("weather-clear", false);

        disabledWorlds = new HashSet<>();
        List<String> dw = getConfig().getStringList("disabled-worlds");
        if (dw != null) {
            for (String w : dw) {
                if (w != null) {
                    disabledWorlds.add(w.trim().toLowerCase());
                }
            }
        }

        showNightSkip = getConfig().getBoolean("message-settings.show-night-skip", true);
        showToggleMsgs = getConfig().getBoolean("message-settings.show-toggle-messages", true);
        consoleLogging = getConfig().getBoolean("message-settings.console-logging", true);

        nightSkipMsg = formatMsg("night-skipped");
        enabledMsg = formatMsg("plugin-enabled");
        disabledMsg = formatMsg("plugin-disabled");
        msgEnabledMsg = formatMsg("msg-enabled", "&aNight skip broadcast messages are now &2enabled&a!");
        msgDisabledMsg = formatMsg("msg-disabled", "&cNight skip broadcast messages are now &4disabled&c!");
        weatherEnabledMsg = formatMsg("weather-enabled", "&aWeather clearing is now &2enabled&a!");
        weatherDisabledMsg = formatMsg("weather-disabled", "&cWeather clearing is now &4disabled&c!");
        reloadedMsg = formatMsg("plugin-reloaded");
        noPermMsg = formatMsg("no-permission");
        statusEnabledMsg = formatMsg("status-enabled");
        statusDisabledMsg = formatMsg("status-disabled");
        usageMsg = formatMsg("usage");
    }

    private String formatMsg(String path) {
        return formatMsg(path, "");
    }

    private String formatMsg(String path, String def) {
        String message = getConfig().getString("messages." + path, def);
        return (message == null ? "" : message)
                .replace("%plugin%", pluginName)
                .replace("&", "§");
    }

    @Override
    public void onDisable() {
        if (consoleLogging) {
            String version = getDescription().getVersion();
            log("");
            log("§c[==================================================]");
            log(String.format("§c[  %-15s v%-6s - Shutting Down      ]", pluginName, version));
            log("§c[==================================================]");
            log("");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("oneplayersleep.toggle")) {
                String msgStatus = showNightSkip ? "ON" : "OFF";
                String weatherStatus = weatherClearing ? "ON" : "OFF";
                String status = (enabled ? statusEnabledMsg : statusDisabledMsg)
                        .replace("%messages_status%", msgStatus)
                        .replace("%weather_status%", weatherStatus);
                sender.sendMessage(status);
                sender.sendMessage(usageMsg);
            } else {
                sender.sendMessage(noPermMsg);
            }
            return true;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "reload" -> {
                if (!sender.hasPermission("oneplayersleep.reload")) {
                    sender.sendMessage(noPermMsg);
                    return true;
                }
                reloadConfig();
                loadConfig();
                sender.sendMessage(reloadedMsg);
                return true;
            }
            case "enable" -> {
                if (!sender.hasPermission("oneplayersleep.toggle")) {
                    sender.sendMessage(noPermMsg);
                    return true;
                }
                enabled = true;
                if (showToggleMsgs) sender.sendMessage(enabledMsg);
                return true;
            }
            case "disable" -> {
                if (!sender.hasPermission("oneplayersleep.toggle")) {
                    sender.sendMessage(noPermMsg);
                    return true;
                }
                enabled = false;
                if (showToggleMsgs) sender.sendMessage(disabledMsg);
                return true;
            }
            case "msg", "message" -> {
                if (!sender.hasPermission("oneplayersleep.toggle")) {
                    sender.sendMessage(noPermMsg);
                    return true;
                }

                if (args.length == 1) {
                    showNightSkip = !showNightSkip;
                } else {
                    String state = args[1].toLowerCase();
                    if (state.equals("on") || state.equals("true") || state.equals("enable")) {
                        showNightSkip = true;
                    } else if (state.equals("off") || state.equals("false") || state.equals("disable")) {
                        showNightSkip = false;
                    } else {
                        sender.sendMessage("§cUsage: /oneplayersleep msg <on|off>");
                        return true;
                    }
                }

                getConfig().set("message-settings.show-night-skip", showNightSkip);
                saveConfig();

                if (showToggleMsgs) {
                    sender.sendMessage(showNightSkip ? msgEnabledMsg : msgDisabledMsg);
                }
                return true;
            }
            case "weather", "weatherclear", "wc" -> {
                if (!sender.hasPermission("oneplayersleep.toggle")) {
                    sender.sendMessage(noPermMsg);
                    return true;
                }

                if (args.length == 1) {
                    weatherClearing = !weatherClearing;
                } else {
                    String state = args[1].toLowerCase();
                    if (state.equals("on") || state.equals("true") || state.equals("enable")) {
                        weatherClearing = true;
                    } else if (state.equals("off") || state.equals("false") || state.equals("disable")) {
                        weatherClearing = false;
                    } else {
                        sender.sendMessage("§cUsage: /oneplayersleep weather <on|off>");
                        return true;
                    }
                }

                getConfig().set("weather-clear", weatherClearing);
                saveConfig();

                if (showToggleMsgs) {
                    sender.sendMessage(weatherClearing ? weatherEnabledMsg : weatherDisabledMsg);
                }
                return true;
            }
            default -> {
                if (!sender.hasPermission("oneplayersleep.toggle")) {
                    sender.sendMessage(noPermMsg);
                    return true;
                }
                sender.sendMessage(usageMsg);
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("oneplayersleep.toggle")) {
                completions.add("enable");
                completions.add("disable");
                completions.add("msg");
                completions.add("weather");
            }
            if (sender.hasPermission("oneplayersleep.reload")) {
                completions.add("reload");
            }
            String input = args[0].toLowerCase();
            return completions.stream()
                    .filter(s -> s.startsWith(input))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            String firstArg = args[0].toLowerCase();
            if (firstArg.equals("msg") || firstArg.equals("message") ||
                firstArg.equals("weather") || firstArg.equals("weatherclear") || firstArg.equals("wc")) {
                if (sender.hasPermission("oneplayersleep.toggle")) {
                    List<String> toggleOptions = List.of("on", "off");
                    String input = args[1].toLowerCase();
                    return toggleOptions.stream()
                            .filter(s -> s.startsWith(input))
                            .collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBed(PlayerBedEnterEvent e) {
        if (!enabled) return;

        if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player p = e.getPlayer();
        World w = p.getWorld();

        // Check if the world is disabled
        if (disabledWorlds.contains(w.getName().toLowerCase())) {
            return;
        }

        if (!isNightOrStormInOverworld(w)) return;

        // Schedule delayed task on player's scheduler for natural bed sleep animation
        schedulerAdapter.runDelayedForPlayer(p, () -> {
            if (!p.isOnline() || !p.isSleeping()) return;
            if (!isNightOrStormInOverworld(w)) return;

            // Execute time and weather change safely on global region scheduler
            schedulerAdapter.runGlobal(() -> {
                if (!isNightOrStormInOverworld(w)) return;

                // Advance world time and increment day count accurately
                long fullTime = w.getFullTime();
                long nextDay = fullTime - (fullTime % 24000L) + 24000L;
                w.setFullTime(nextDay);
                w.setTime(0);

                if (weatherClearing) {
                    w.setStorm(false);
                    w.setThundering(false);
                }

                // Wake up all sleeping players and reset insomnia/phantom counters
                for (Player pl : w.getPlayers()) {
                    if (pl.isSleeping()) {
                        schedulerAdapter.runForPlayer(pl, () -> {
                            if (pl.isSleeping()) {
                                pl.wakeup(false);
                            }
                            try {
                                pl.setStatistic(Statistic.TIME_SINCE_REST, 0);
                            } catch (Throwable ignored) {}
                        });
                    }
                }

                if (showNightSkip) {
                    String msg = nightSkipMsg
                            .replace("%player%", p.getName())
                            .replace("%displayname%", p.getDisplayName())
                            .replace("%world%", w.getName());
                    for (Player pl : w.getPlayers()) {
                        pl.sendMessage(msg);
                    }
                }
            });
        }, sleepDelayTicks);
    }

    private boolean isNightOrStormInOverworld(World w) {
        if (w == null || w.getEnvironment() != World.Environment.NORMAL) return false;
        long time = w.getTime();
        boolean isNight = time >= 12540 && time <= 23999;
        boolean isStorm = w.isThundering();
        return isNight || isStorm;
    }

    public SchedulerAdapter getSchedulerAdapter() {
        return schedulerAdapter;
    }
}
