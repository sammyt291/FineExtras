package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Set player swim speed
 * Usage: /swimspeed <value|reset> [player]
 * 
 * Uses Attribute.WATER_MOVEMENT_EFFICIENCY (added in 1.21)
 * - Scale: 0-10 (converted to 0-1 internally)
 * - Default is 0 (no swim speed bonus)
 * - 10 = maximum efficiency (similar to Dolphin's Grace effect)
 */
public class SwimSpeedCommand extends BaseCommand {

    private static final double DEFAULT_SWIM_SPEED = 0.0;

    public SwimSpeedCommand(FineExtras plugin) {
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

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.PREFIX + "§cUsage: " + getUsage());
            return true;
        }

        Player target;
        String valueArg = args[0];

        // Check if resetting
        if (valueArg.equalsIgnoreCase("reset")) {
            if (args.length > 1) {
                if (!sender.hasPermission("fineextras.swimspeed.others")) {
                    if (sender instanceof Player) {
                        MessageUtil.sendError((Player) sender, "You don't have permission to change other players' swim speed.");
                    }
                    return true;
                }
                target = getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(MessageUtil.PREFIX + "§cPlayer §e" + args[1] + " §cis not online.");
                    return true;
                }
            } else {
                if (!isPlayer(sender)) {
                    return true;
                }
                target = (Player) sender;
            }

            // Reset swim speed to default
            AttributeInstance swimAttr = target.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY);
            if (swimAttr != null) {
                swimAttr.setBaseValue(DEFAULT_SWIM_SPEED);
            }

            if (target.equals(sender)) {
                MessageUtil.sendSuccess((Player) sender, "Reset your swim speed to default.");
            } else {
                MessageUtil.sendSuccess((Player) sender, "Reset §e" + target.getName() + "§a's swim speed to default.");
                MessageUtil.sendSuccess(target, "Your swim speed was reset by §e" + sender.getName() + "§a.");
            }
            return true;
        }

        // Parse speed value
        double speed;
        try {
            speed = Double.parseDouble(valueArg);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.PREFIX + "§cInvalid speed value. Use a number between 0 and 10, or 'reset'.");
            return true;
        }

        // Validate speed (0-10 scale)
        if (speed < 0 || speed > 10) {
            sender.sendMessage(MessageUtil.PREFIX + "§cSpeed must be between 0 and 10.");
            return true;
        }

        // Get target player
        if (args.length > 1) {
            if (!sender.hasPermission("fineextras.swimspeed.others")) {
                if (sender instanceof Player) {
                    MessageUtil.sendError((Player) sender, "You don't have permission to change other players' swim speed.");
                }
                return true;
            }
            target = getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MessageUtil.PREFIX + "§cPlayer §e" + args[1] + " §cis not online.");
                return true;
            }
        } else {
            if (!isPlayer(sender)) {
                return true;
            }
            target = (Player) sender;
        }

        // Set swim speed using attribute system
        // Convert 0-10 scale to 0-1 (water_movement_efficiency is 0-1)
        AttributeInstance swimAttr = target.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY);
        if (swimAttr == null) {
            sender.sendMessage(MessageUtil.PREFIX + "§cCould not access swim speed attribute.");
            return true;
        }

        double swimSpeed = speed / 10.0;
        swimAttr.setBaseValue(swimSpeed);

        if (target.equals(sender)) {
            MessageUtil.sendSuccess((Player) sender, "Set your swim speed to §e" + speed + "§a.");
        } else {
            MessageUtil.sendSuccess((Player) sender, "Set §e" + target.getName() + "§a's swim speed to §e" + speed + "§a.");
            MessageUtil.sendSuccess(target, "Your swim speed was set to §e" + speed + "§a by §e" + sender.getName() + "§a.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "reset"));
            return filterCompletions(completions, args[0]);
        } else if (args.length == 2 && sender.hasPermission("fineextras.swimspeed.others")) {
            return filterCompletions(getOnlinePlayerNames(), args[1]);
        }
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "swimspeed";
    }

    @Override
    public String getDescription() {
        return "Set your swim speed";
    }

    @Override
    public String getUsage() {
        return "/swimspeed <0-10|reset> [player]";
    }

    @Override
    public String getPermission() {
        return "fineextras.swimspeed";
    }
}
