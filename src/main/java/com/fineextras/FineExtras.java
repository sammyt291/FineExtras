package com.fineextras;

import com.fineextras.commands.*;
import com.fineextras.scheduler.TaskScheduler;
import com.fineextras.util.TeleportRequestManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class FineExtras extends JavaPlugin {

    private static FineExtras instance;
    private TaskScheduler scheduler;
    private TeleportRequestManager teleportRequestManager;
    private Set<String> registeredCommands = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;
        scheduler = new TaskScheduler(this);
        teleportRequestManager = new TeleportRequestManager(this);

        // Register the main /fineextras command (always registers)
        FineExtrasCommand fineExtrasCommand = new FineExtrasCommand(this);
        getCommand("fineextras").setExecutor(fineExtrasCommand);
        getCommand("fineextras").setTabCompleter(fineExtrasCommand);

        // Register direct commands only if not already taken by another plugin
        registerCommandIfAvailable("tp2p", new Tp2pCommand(this));
        registerCommandIfAvailable("ci", new ClearInventoryCommand(this));
        registerCommandIfAvailable("clearinventory", new ClearInventoryCommand(this));
        registerCommandIfAvailable("gmc", new GamemodeCommand(this, "creative"));
        registerCommandIfAvailable("gms", new GamemodeCommand(this, "survival"));
        registerCommandIfAvailable("gma", new GamemodeCommand(this, "adventure"));
        registerCommandIfAvailable("gmsp", new GamemodeCommand(this, "spectator"));
        registerCommandIfAvailable("tpa", new TpaCommand(this));
        registerCommandIfAvailable("tpr", new TprCommand(this));
        registerCommandIfAvailable("speed", new SpeedCommand(this));
        registerCommandIfAvailable("gravity", new GravityCommand(this));
        registerCommandIfAvailable("flyspeed", new FlySpeedCommand(this));
        registerCommandIfAvailable("walkspeed", new WalkSpeedCommand(this));
        registerCommandIfAvailable("runspeed", new WalkSpeedCommand(this));
        registerCommandIfAvailable("sprintspeed", new WalkSpeedCommand(this));
        registerCommandIfAvailable("swimspeed", new SwimSpeedCommand(this));

        getLogger().info("FineExtras has been enabled!");
        getLogger().info("Running on " + (TaskScheduler.isFolia() ? "Folia" : "Spigot/Paper"));
        
        if (!registeredCommands.isEmpty()) {
            getLogger().info("Registered direct commands: " + String.join(", ", registeredCommands));
        }
    }

    @Override
    public void onDisable() {
        if (teleportRequestManager != null) {
            teleportRequestManager.clearAllRequests();
        }
        getLogger().info("FineExtras has been disabled!");
    }

    /**
     * Register a command only if no other plugin has registered it
     */
    private void registerCommandIfAvailable(String commandName, BaseCommand executor) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            return;
        }

        // Check if another plugin has already registered this command
        if (isCommandRegisteredByOtherPlugin(commandName)) {
            getLogger().info("Command /" + commandName + " is already registered by another plugin, skipping.");
            return;
        }

        command.setExecutor(executor);
        command.setTabCompleter(executor);
        registeredCommands.add(commandName);
    }

    /**
     * Check if a command is registered by another plugin
     */
    private boolean isCommandRegisteredByOtherPlugin(String commandName) {
        try {
            // Get the server's command map
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // Check known commands
            org.bukkit.command.Command existingCommand = commandMap.getCommand(commandName);
            if (existingCommand != null) {
                // If it's a PluginCommand, check if it belongs to us
                if (existingCommand instanceof PluginCommand pluginCommand) {
                    return !pluginCommand.getPlugin().equals(this);
                }
                // If it's not a PluginCommand but exists, it's registered by something else
                return true;
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Could not check command registration for: " + commandName, e);
        }
        return false;
    }

    public static FineExtras getInstance() {
        return instance;
    }

    public TaskScheduler getScheduler() {
        return scheduler;
    }

    public TeleportRequestManager getTeleportRequestManager() {
        return teleportRequestManager;
    }

    public Set<String> getRegisteredCommands() {
        return registeredCommands;
    }
}
