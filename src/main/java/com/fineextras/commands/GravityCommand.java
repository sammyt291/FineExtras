package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Set player gravity
 * Usage: /gravity <player> <value|reset>
 */
public class GravityCommand extends BaseCommand {

    public GravityCommand(FineExtras plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            if (sender instanceof Player) {
                MessageUtil.sendError((Player) sender, "You don't have permission to use this command.");
            }
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.PREFIX + "§cUsage: " + getUsage());
            return true;
        }

        Player target = getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.PREFIX + "§cPlayer §e" + args[0] + " §cis not online.");
            return true;
        }

        String valueArg = args[1];

        if (valueArg.equalsIgnoreCase("reset")) {
            // Reset gravity to default (enabled)
            target.setGravity(true);
            
            if (target.equals(sender)) {
                MessageUtil.sendSuccess((Player) sender, "Reset your gravity to default.");
            } else {
                MessageUtil.sendSuccess((Player) sender, "Reset §e" + target.getName() + "§a's gravity to default.");
                MessageUtil.sendSuccess(target, "Your gravity was reset by §e" + sender.getName() + "§a.");
            }
            return true;
        }

        // Parse gravity value
        float gravityValue;
        try {
            gravityValue = Float.parseFloat(valueArg);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.PREFIX + "§cInvalid gravity value. Use a number or 'reset'.");
            return true;
        }

        // Gravity in Minecraft is boolean - true/false
        // A value of 0 disables gravity, any other value enables it
        // We use the float for informational purposes but Bukkit only supports on/off
        boolean hasGravity = gravityValue != 0;
        target.setGravity(hasGravity);

        String gravityStatus = hasGravity ? "enabled" : "disabled";
        
        if (target.equals(sender)) {
            MessageUtil.sendSuccess((Player) sender, "Set your gravity to §e" + gravityStatus + "§a (value: " + gravityValue + ").");
        } else {
            MessageUtil.sendSuccess((Player) sender, "Set §e" + target.getName() + "§a's gravity to §e" + gravityStatus + "§a (value: " + gravityValue + ").");
            MessageUtil.sendSuccess(target, "Your gravity was set to §e" + gravityStatus + "§a by §e" + sender.getName() + "§a.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(), args[0]);
        } else if (args.length == 2) {
            return filterCompletions(Arrays.asList("0", "0.5", "1", "1.5", "2", "reset"), args[1]);
        }
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "gravity";
    }

    @Override
    public String getDescription() {
        return "Set a player's gravity (0 = no gravity, any other value = normal gravity)";
    }

    @Override
    public String getUsage() {
        return "/gravity <player> <value|reset>";
    }

    @Override
    public String getPermission() {
        return "fineextras.gravity";
    }
}
