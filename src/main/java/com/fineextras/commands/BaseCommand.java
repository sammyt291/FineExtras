package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for all commands
 */
public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected final FineExtras plugin;

    public BaseCommand(FineExtras plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return tabComplete(sender, args);
    }

    /**
     * Execute the command
     */
    public abstract boolean execute(CommandSender sender, String[] args);

    /**
     * Get tab completions
     */
    public abstract List<String> tabComplete(CommandSender sender, String[] args);

    /**
     * Get the command name (for subcommand use)
     */
    public abstract String getName();

    /**
     * Get the command description
     */
    public abstract String getDescription();

    /**
     * Get the command usage
     */
    public abstract String getUsage();

    /**
     * Get the permission node
     */
    public abstract String getPermission();

    /**
     * Check if sender has permission
     */
    protected boolean hasPermission(CommandSender sender) {
        String permission = getPermission();
        if (permission == null || permission.isEmpty()) {
            return true;
        }
        return sender.hasPermission(permission);
    }

    /**
     * Check if sender is a player
     */
    protected boolean isPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.PREFIX + "§cThis command can only be used by players.");
            return false;
        }
        return true;
    }

    /**
     * Get online player names for tab completion
     */
    protected List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .collect(Collectors.toList());
    }

    /**
     * Filter tab completions based on current input
     */
    protected List<String> filterCompletions(List<String> completions, String current) {
        if (current == null || current.isEmpty()) {
            return completions;
        }
        String lowerCurrent = current.toLowerCase();
        return completions.stream()
            .filter(s -> s.toLowerCase().startsWith(lowerCurrent))
            .collect(Collectors.toList());
    }

    /**
     * Get a player by name
     */
    protected Player getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }
}
