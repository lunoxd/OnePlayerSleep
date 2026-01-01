package com.ops;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public class OnePlayerSleep extends JavaPlugin implements Listener {
    private String v;
    private Method m;
    private boolean u;

    @Override
    public void onEnable() {
        v = Bukkit.getVersion();
        d();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Enabled for " + v);
    }

    private void d() {
        try {
            String p = Bukkit.getServer().getClass().getPackage().getName();
            String[] s = p.split("\\.");
            if (s.length >= 4) {
                String ver = s[3];
                u = !ver.startsWith("v1_16") && !ver.startsWith("v1_17") && !ver.startsWith("v1_18");
            } else {
                u = true;
            }
        } catch (Exception e) {
            u = true;
        }

        try {
            m = World.class.getMethod("setStorm", boolean.class);
        } catch (Exception e) {
            m = null;
        }
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent e) {
        if (e.isCancelled()) return;

        try {
            if (u) {
                if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
            }
        } catch (Exception ex) {
        }

        Player p = e.getPlayer();
        World w = p.getWorld();

        if (!i(w)) return;

        Bukkit.getScheduler().runTaskLater(this, () -> {
            try {
                if (!p.isSleeping()) return;

                w.setTime(0);
                w.setStorm(false);
                w.setThundering(false);

                if (m != null) {
                    try {
                        m.invoke(w, false);
                    } catch (Exception ex) {
                    }
                }

                for (Player pl : w.getPlayers()) {
                    try {
                        if (pl.isSleeping()) {
                            Method wb = Player.class.getMethod("wakeup", boolean.class);
                            wb.invoke(pl, false);
                        }
                    } catch (Exception ex) {
                        try {
                            Player.class.getMethod("setBedSpawnLocation", org.bukkit.Location.class, boolean.class);
                            if (pl.isSleeping()) {
                                pl.damage(0);
                            }
                        } catch (Exception exx) {
                        }
                    }
                }

                w.getPlayers().forEach(pl -> {
                    pl.sendMessage("§6Night skipped by " + p.getName());
                });

            } catch (Exception ex) {
            }
        }, 10L);
    }

    private boolean i(World w) {
        try {
            String n = w.getEnvironment().name();
            if (n.equals("NETHER") || n.equals("THE_END")) return false;
            long t = w.getTime();
            return t >= 12541 && t <= 23458;
        } catch (Exception e) {
            return false;
        }
    }
}
