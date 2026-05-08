package com.ops;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnePlayerSleep extends JavaPlugin implements Listener {
    private String v;
    private Method globalRegionSchedulerMethod;
    private Method globalRegionExecuteMethod;
    private Method entityGetSchedulerMethod;
    private Method entityRunDelayedMethod;
    private Method entityExecuteMethod;

    private boolean enabled = true;
    private String pluginName;
    private boolean weatherClearing;

    private boolean showNightSkip;
    private boolean showToggleMsgs;
    private boolean consoleLogging;
    private String nightSkipMsg;
    private String enabledMsg;
    private String disabledMsg;
    private String reloadedMsg;
    private String noPermMsg;
    private String statusEnabledMsg;
    private String statusDisabledMsg;
    private String usageMsg;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        detectSchedulers();

        v = Bukkit.getVersion();
        getServer().getPluginManager().registerEvents(this, this);
        java.util.Objects.requireNonNull(getCommand("oneplayersleep"), "oneplayersleep command missing").setExecutor(this);

        if (consoleLogging) {
            log("");
            log("§b╔══════════════════════════════════════════════════╗");
            log("§b║                                                  §b║");
            log("§b║    §f⭐ §e" + pluginName + " §6v4.0.1 §f⭐                §b║");
            log("§b║                                                  §b║");
            log("§b║    §7Author: §fheyWaffie                           §b║");
            log("§b║    §7Server: §f" + v.substring(0, Math.min(v.length(), 20)) + "              §b║");
            log("§b║                                                  §b║");
            log("§b╠══════════════════════════════════════════════════╣");
            log("§b║                                                  §b║");
            log("§b║    §a✓ §fEvent Listeners §a→ §2LOADED              §b║");
            log("§b║    §a✓ §fCommands §a→ §2REGISTERED                  §b║");
            log("§b║    §a✓ §fConfiguration §a→ §2INITIALIZED            §b║");
            log("§b║                                                  §b║");
            log("§b╠══════════════════════════════════════════════════╣");
            log("§b║                                                  §b║");
            log("§b║    §d🌙 §fOne Player Sleep §d→ §5Enabled!          §b║");
            log("§b║    §6💤 §fSkip nights with just §6ONE §fplayer!      §b║");
            log("§b║                                                  §b║");
            log("§b╚══════════════════════════════════════════════════╝");
            log("");
        }
    }
    
    private void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }
    
    private void loadConfig() {
        pluginName = getConfig().getString("plugin-name", "OnePlayerSleep");
        enabled = getConfig().getBoolean("enabled-on-startup", true);
        weatherClearing = getConfig().getBoolean("weather-clear", false);

        showNightSkip = getConfig().getBoolean("message-settings.show-night-skip", true);
        showToggleMsgs = getConfig().getBoolean("message-settings.show-toggle-messages", true);
        consoleLogging = getConfig().getBoolean("message-settings.console-logging", true);

        nightSkipMsg = formatMsg("night-skipped");
        enabledMsg = formatMsg("plugin-enabled");
        disabledMsg = formatMsg("plugin-disabled");
        reloadedMsg = formatMsg("plugin-reloaded");
        noPermMsg = formatMsg("no-permission");
        statusEnabledMsg = formatMsg("status-enabled");
        statusDisabledMsg = formatMsg("status-disabled");
        usageMsg = formatMsg("usage");
    }

    private void detectSchedulers() {
        try {
            globalRegionSchedulerMethod = Bukkit.class.getMethod("getGlobalRegionScheduler");
            Object scheduler = globalRegionSchedulerMethod.invoke(null);
            globalRegionExecuteMethod = scheduler.getClass().getMethod("execute", org.bukkit.plugin.Plugin.class, Runnable.class);
        } catch (ReflectiveOperationException ignored) {
            globalRegionSchedulerMethod = null;
            globalRegionExecuteMethod = null;
        }

        try {
            entityGetSchedulerMethod = Player.class.getMethod("getScheduler");
            Class<?> schedulerType = entityGetSchedulerMethod.getReturnType();
            entityRunDelayedMethod = schedulerType.getMethod("runDelayed", org.bukkit.plugin.Plugin.class, Runnable.class, Runnable.class, long.class);
            entityExecuteMethod = schedulerType.getMethod("execute", org.bukkit.plugin.Plugin.class, Runnable.class, Runnable.class);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            entityGetSchedulerMethod = null;
            entityRunDelayedMethod = null;
            entityExecuteMethod = null;
        }
    }
    
    private String formatMsg(String path) {
        String message = getConfig().getString("messages." + path, "");
        return (message == null ? "" : message)
                .replace("%plugin%", pluginName)
                .replace("&", "§");
    }
    
    @Override
    public void onDisable() {
        if (consoleLogging) {
            log("");
            log("§c╔══════════════════════════════════════════════════╗");
            log("§c║                                                  §c║");
            log("§c║    §e⭐ §6" + pluginName + " §cv4.0.1 - §4Shutting Down §e⭐  §c║");
            log("§c║                                                  §c║");
            log("§c╠══════════════════════════════════════════════════╣");
            log("§c║                                                  §c║");
            log("§c║    §f🌟 §7Thanks for using §f" + pluginName + "§7!         §c║");
            log("§c║    §f💙 §7Your players will miss the quick nights!  §c║");
            log("§c║                                                  §c║");
            log("§c╚══════════════════════════════════════════════════╝");
            log("");
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("oneplayersleep.toggle")) {
                sender.sendMessage(enabled ? statusEnabledMsg : statusDisabledMsg);
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

    @EventHandler
    public void onBed(PlayerBedEnterEvent e) {
        if (!enabled || e.isCancelled()) return;

        if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player p = e.getPlayer();
        World w = p.getWorld();

        if (!isNightInOverworld(w)) return;

        runLaterForPlayer(p, () -> {
            if (!p.isSleeping()) return;

            runGlobal(() -> {
                w.setTime(0);

                if (weatherClearing) {
                    w.setStorm(false);
                    w.setThundering(false);
                }

                for (Player pl : w.getPlayers()) {
                    if (pl.isSleeping()) {
                        wakeupPlayer(pl);
                    }
                }

                if (showNightSkip) {
                    String msg = nightSkipMsg.replace("%player%", p.getName());
                    w.getPlayers().forEach(pl -> pl.sendMessage(msg));
                }
            });
        }, 100L);
    }

    private void runLaterForPlayer(Player player, Runnable task, long delayTicks) {
        if (entityGetSchedulerMethod != null && entityRunDelayedMethod != null) {
            try {
                Object scheduler = entityGetSchedulerMethod.invoke(player);
                if (scheduler != null) {
                    entityRunDelayedMethod.invoke(scheduler, this, task, null, delayTicks);
                    return;
                }
            } catch (ReflectiveOperationException ignored) {
                // Fall back to the standard scheduler.
            }
        }

        Bukkit.getScheduler().runTaskLater(this, task, delayTicks);
    }

    private void runGlobal(Runnable task) {
        if (globalRegionSchedulerMethod != null && globalRegionExecuteMethod != null) {
            try {
                Object scheduler = globalRegionSchedulerMethod.invoke(null);
                globalRegionExecuteMethod.invoke(scheduler, this, task);
                return;
            } catch (ReflectiveOperationException ignored) {
                // Fall back to the standard scheduler.
            }
        }

        task.run();
    }

    private void wakeupPlayer(Player player) {
        if (entityGetSchedulerMethod != null && entityExecuteMethod != null) {
            try {
                Object scheduler = entityGetSchedulerMethod.invoke(player);
                if (scheduler != null) {
                    Boolean scheduled = (Boolean) entityExecuteMethod.invoke(scheduler, this, (Runnable) () -> {
                        if (player.isSleeping()) {
                            player.wakeup(false);
                        }
                    }, null);
                    if (Boolean.TRUE.equals(scheduled)) {
                        return;
                    }
                }
            } catch (ReflectiveOperationException ignored) {
                // Fall through to direct wakeup on non-Folia servers.
            }
        }

        player.wakeup(false);
    }

    private boolean isNightInOverworld(World w) {
        if (w.getEnvironment() != World.Environment.NORMAL) return false;
        long time = w.getTime();
        return time >= 12541 && time <= 23458;
    }
}
