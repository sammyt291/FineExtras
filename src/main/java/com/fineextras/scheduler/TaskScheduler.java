package com.fineextras.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * Abstract scheduler that works on Spigot, Paper, and Folia.
 * Automatically detects the server type and uses the appropriate scheduler.
 */
public class TaskScheduler {

    private static Boolean isFolia = null;
    private final Plugin plugin;

    public TaskScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Check if we're running on Folia
     */
    public static boolean isFolia() {
        if (isFolia == null) {
            try {
                Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
                isFolia = true;
            } catch (ClassNotFoundException e) {
                isFolia = false;
            }
        }
        return isFolia;
    }

    /**
     * Run a task on the main thread (or region thread for Folia)
     */
    public void runTask(Runnable task) {
        if (isFolia()) {
            Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Run a task for a specific entity (uses entity scheduler on Folia)
     */
    public void runEntityTask(Entity entity, Runnable task) {
        if (isFolia()) {
            entity.getScheduler().run(plugin, scheduledTask -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Run a task later (delayed)
     */
    public void runTaskLater(Runnable task, long delayTicks) {
        if (isFolia()) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> task.run(), delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    /**
     * Run a task later for a specific entity
     */
    public void runEntityTaskLater(Entity entity, Runnable task, long delayTicks) {
        if (isFolia()) {
            entity.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    /**
     * Run a repeating task
     */
    public ScheduledTask runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        if (isFolia()) {
            io.papermc.paper.threadedregions.scheduler.ScheduledTask foliaTask = 
                Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), delayTicks, periodTicks);
            return new ScheduledTask(foliaTask);
        } else {
            int taskId = Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks).getTaskId();
            return new ScheduledTask(taskId, plugin);
        }
    }

    /**
     * Run an async task
     */
    public void runTaskAsync(Runnable task) {
        if (isFolia()) {
            Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /**
     * Run an async task later
     */
    public void runTaskLaterAsync(Runnable task, long delayTicks) {
        if (isFolia()) {
            // Folia async scheduler uses real time, convert ticks to ms (1 tick = 50ms)
            Bukkit.getAsyncScheduler().runDelayed(plugin, scheduledTask -> task.run(), delayTicks * 50, TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
        }
    }

    /**
     * Wrapper class for scheduled tasks that can be cancelled
     */
    public static class ScheduledTask {
        private io.papermc.paper.threadedregions.scheduler.ScheduledTask foliaTask;
        private int bukkitTaskId = -1;
        private Plugin plugin;

        public ScheduledTask(io.papermc.paper.threadedregions.scheduler.ScheduledTask foliaTask) {
            this.foliaTask = foliaTask;
        }

        public ScheduledTask(int bukkitTaskId, Plugin plugin) {
            this.bukkitTaskId = bukkitTaskId;
            this.plugin = plugin;
        }

        public void cancel() {
            if (foliaTask != null) {
                foliaTask.cancel();
            } else if (bukkitTaskId != -1) {
                Bukkit.getScheduler().cancelTask(bukkitTaskId);
            }
        }
    }
}
