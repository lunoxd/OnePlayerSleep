package com.ops;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Universal scheduler adapter providing seamless multi-threaded scheduling
 * across Folia (Regionized Threading) and standard Paper / Purpur / Spigot servers.
 */
public class SchedulerAdapter {
    private final Plugin plugin;
    private final boolean folia;

    // Folia Global Region Scheduler reflection handles
    private Object globalRegionScheduler;
    private Method globalRegionRunMethod;

    // Folia Entity Scheduler reflection handles
    private Method entityGetSchedulerMethod;
    private Method entityRunMethod;
    private Method entityRunDelayedMethod;

    public SchedulerAdapter(Plugin plugin) {
        this.plugin = plugin;
        this.folia = detectFolia();

        if (this.folia) {
            initFoliaReflection();
        }
    }

    private boolean detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            try {
                Bukkit.class.getMethod("getGlobalRegionScheduler");
                return true;
            } catch (NoSuchMethodException ex) {
                return false;
            }
        }
    }

    private void initFoliaReflection() {
        try {
            Method getGlobalSchedulerMethod = Bukkit.class.getMethod("getGlobalRegionScheduler");
            this.globalRegionScheduler = getGlobalSchedulerMethod.invoke(null);
            if (this.globalRegionScheduler != null) {
                this.globalRegionRunMethod = this.globalRegionScheduler.getClass()
                        .getMethod("run", Plugin.class, Consumer.class);
            }
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Failed to resolve Folia GlobalRegionScheduler methods", t);
        }

        try {
            this.entityGetSchedulerMethod = Player.class.getMethod("getScheduler");
            Class<?> entitySchedulerClass = this.entityGetSchedulerMethod.getReturnType();

            try {
                this.entityRunMethod = entitySchedulerClass.getMethod("run", Plugin.class, Consumer.class, Runnable.class);
            } catch (NoSuchMethodException ignored) {}

            try {
                this.entityRunDelayedMethod = entitySchedulerClass.getMethod("runDelayed", Plugin.class, Consumer.class, Runnable.class, long.class);
            } catch (NoSuchMethodException ignored) {}
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Failed to resolve Folia EntityScheduler methods", t);
        }
    }

    /**
     * Checks if the server environment is running Folia.
     */
    public boolean isFolia() {
        return folia;
    }

    /**
     * Executes a task on the global region scheduler (safe for world time and weather updates).
     */
    public void runGlobal(Runnable task) {
        if (folia && globalRegionScheduler != null && globalRegionRunMethod != null) {
            try {
                Consumer<Object> taskConsumer = scheduledTask -> task.run();
                globalRegionRunMethod.invoke(globalRegionScheduler, plugin, taskConsumer);
                return;
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Failed to execute global region task on Folia", t);
            }
        }

        // Standard Paper / Spigot execution
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Executes a delayed task on an entity's region thread.
     */
    public void runDelayedForPlayer(Player player, Runnable task, long delayTicks) {
        if (folia && entityGetSchedulerMethod != null && entityRunDelayedMethod != null) {
            try {
                Object entityScheduler = entityGetSchedulerMethod.invoke(player);
                if (entityScheduler != null) {
                    Consumer<Object> taskConsumer = scheduledTask -> task.run();
                    Object scheduled = entityRunDelayedMethod.invoke(entityScheduler, plugin, taskConsumer, null, delayTicks);
                    if (scheduled != null) {
                        return;
                    }
                }
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Failed to schedule delayed entity task on Folia", t);
            }
        }

        // Standard Paper / Spigot execution
        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    /**
     * Executes a task on an entity's region thread as soon as possible.
     */
    public void runForPlayer(Player player, Runnable task) {
        if (folia && entityGetSchedulerMethod != null) {
            try {
                Object entityScheduler = entityGetSchedulerMethod.invoke(player);
                if (entityScheduler != null) {
                    if (entityRunMethod != null) {
                        Consumer<Object> taskConsumer = scheduledTask -> task.run();
                        Object scheduled = entityRunMethod.invoke(entityScheduler, plugin, taskConsumer, null);
                        if (scheduled != null) {
                            return;
                        }
                    } else if (entityRunDelayedMethod != null) {
                        Consumer<Object> taskConsumer = scheduledTask -> task.run();
                        Object scheduled = entityRunDelayedMethod.invoke(entityScheduler, plugin, taskConsumer, null, 1L);
                        if (scheduled != null) {
                            return;
                        }
                    }
                }
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Failed to execute entity task on Folia", t);
            }
        }

        // Standard Paper / Spigot execution
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
}
