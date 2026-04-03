package com.ops;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnePlayerSleep extends JavaPlugin implements Listener, CommandExecutor {
    // Server version info
    private String v;
    private Method stormMethod;
    private Method wakeupMethod;
    private boolean useNewAPI;
    
    // Plugin state
    private boolean enabled = true;
    private String pluginName;
    private boolean weatherClearing;
    
    // Cached config values for performance
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
        // Save default config if not exists
        saveDefaultConfig();
        
        // Load plugin name and settings from config
        loadConfig();
        
        v = Bukkit.getVersion();
        d();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("oneplayersleep").setExecutor(this);
        
        // Aesthetic startup logging
        if (consoleLogging) {
            log("");
            log("§b╔══════════════════════════════════════════════════╗");
            log("§b║                                                  §b║");
            log("§b║    §f⭐ §e" + pluginName + " §6v3.6.4 §f⭐                §b║");
            log("§b║                                                  §b║");
            log("§b║    §7Author: §fOPS                                §b║");
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
        
        // Cache message settings for performance
        showNightSkip = getConfig().getBoolean("message-settings.show-night-skip", true);
        showToggleMsgs = getConfig().getBoolean("message-settings.show-toggle-messages", true);
        consoleLogging = getConfig().getBoolean("message-settings.console-logging", true);
        
        // Cache all messages with colors pre-processed
        nightSkipMsg = formatMsg("night-skipped");
        enabledMsg = formatMsg("plugin-enabled");
        disabledMsg = formatMsg("plugin-disabled");
        reloadedMsg = formatMsg("plugin-reloaded");
        noPermMsg = formatMsg("no-permission");
        statusEnabledMsg = formatMsg("status-enabled");
        statusDisabledMsg = formatMsg("status-disabled");
        usageMsg = formatMsg("usage");
    }
    
    private String formatMsg(String path) {
        return getConfig().getString("messages." + path, "")
                .replace("%plugin%", pluginName)
                .replace("&", "§");
    }
    
    @Override
    public void onDisable() {
        if (consoleLogging) {
            log("");
            log("§c╔══════════════════════════════════════════════════╗");
            log("§c║                                                  §c║");
            log("§c║    §e⭐ §6" + pluginName + " §cv3.6.4 - §4Shutting Down §e⭐  §c║");
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
    
    private void d() {
        // Detect server version for API compatibility
        try {
            String p = Bukkit.getServer().getClass().getPackage().getName();
            String[] s = p.split("\\.");
            if (s.length >= 4) {
                String ver = s[3];
                useNewAPI = !ver.startsWith("v1_16") && !ver.startsWith("v1_17") && !ver.startsWith("v1_18");
            } else {
                useNewAPI = true;
            }
        } catch (Exception e) {
            useNewAPI = true;
        }

        // Cache reflection methods for performance
        try {
            stormMethod = World.class.getMethod("setStorm", boolean.class);
        } catch (Exception e) {
            stormMethod = null;
        }
        
        try {
            wakeupMethod = Player.class.getMethod("wakeup", boolean.class);
        } catch (Exception e) {
            wakeupMethod = null;
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
        
        if (subCmd.equals("reload")) {
            if (!sender.hasPermission("oneplayersleep.reload")) {
                sender.sendMessage(noPermMsg);
                return true;
            }
            reloadConfig();
            loadConfig();
            sender.sendMessage(reloadedMsg);
            return true;
        }
        
        if (!sender.hasPermission("oneplayersleep.toggle")) {
            sender.sendMessage(noPermMsg);
            return true;
        }

        if (subCmd.equals("enable")) {
            enabled = true;
            if (showToggleMsgs) sender.sendMessage(enabledMsg);
            return true;
        } else if (subCmd.equals("disable")) {
            enabled = false;
            if (showToggleMsgs) sender.sendMessage(disabledMsg);
            return true;
        } else {
            sender.sendMessage(usageMsg);
            return true;
        }
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent e) {
        if (!enabled || e.isCancelled()) return;

        // Check bed enter result for newer versions
        if (useNewAPI && e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player p = e.getPlayer();
        World w = p.getWorld();

        // Check if it's night time in overworld
        if (!isNightInOverworld(w)) return;

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (!p.isSleeping()) return;

            w.setTime(0);
            
            if (weatherClearing) {
                w.setStorm(false);
                w.setThundering(false);
                if (stormMethod != null) {
                    try {
                        stormMethod.invoke(w, false);
                    } catch (Exception ignored) {}
                }
            }

            for (Player pl : w.getPlayers()) {
                if (pl.isSleeping()) {
                    if (wakeupMethod != null) {
                        try {
                            wakeupMethod.invoke(pl, false);
                        } catch (Exception ignored) {
                            pl.damage(0);
                        }
                    } else {
                        pl.damage(0);
                    }
                }
            }

            if (showNightSkip) {
                String msg = nightSkipMsg.replace("%player%", p.getName());
                w.getPlayers().forEach(pl -> pl.sendMessage(msg));
            }
        }, 100L);
    }

    private boolean isNightInOverworld(World w) {
        if (w.getEnvironment() != World.Environment.NORMAL) return false;
        long time = w.getTime();
        return time >= 12541 && time <= 23458;
    }
}
