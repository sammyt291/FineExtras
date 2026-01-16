package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Set player gravity using the attribute system
 * Usage: /gravity <value|reset> [player]
 *        /gravity <player> <value|reset>
 * 
 * Uses Attribute.GENERIC_GRAVITY (default: 0.08)
 * - 0 = no gravity (float in place)
 * - 0.08 = normal gravity
 * - Higher values = stronger gravity (fall faster)
 * - Negative values = reverse gravity (float upward)
 */
public class GravityCommand extends BaseCommand {

    private static final double DEFAULT_GRAVITY = 0.08;

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

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.PREFIX + "§cUsage: " + getUsage());
            return true;
        }

        Player target;
        String valueArg;

        if (args.length == 1) {
            valueArg = args[0];
            if (!isPlayer(sender)) {
                return true;
            }
            target = (Player) sender;
        } else {
            boolean firstIsValue = isNumericValue(args[0]) || args[0].equalsIgnoreCase("reset");
            if (firstIsValue) {
                valueArg = args[0];
                target = resolveTarget(sender, args[1]);
            } else {
                valueArg = args[1];
                target = resolveTarget(sender, args[0]);
            }
            if (target == null) {
                return true;
            }
        }

        // Check if resetting
        if (valueArg.equalsIgnoreCase("reset")) {
            // Reset gravity to default
            LivingEntity livingEntity = target;
            AttributeInstance gravityAttr = livingEntity.getAttribute(Attribute.GENERIC_GRAVITY);
            if (gravityAttr != null) {
                gravityAttr.setBaseValue(DEFAULT_GRAVITY);
            }

            if (target.equals(sender)) {
                MessageUtil.sendSuccess((Player) sender, "Reset your gravity to default (" + DEFAULT_GRAVITY + ").");
            } else {
                MessageUtil.sendSuccess((Player) sender, "Reset §e" + target.getName() + "§a's gravity to default.");
                MessageUtil.sendSuccess(target, "Your gravity was reset by §e" + sender.getName() + "§a.");
            }
            return true;
        }

        // Parse gravity value
        double gravityValue;
        try {
            gravityValue = Double.parseDouble(valueArg);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.PREFIX + "§cInvalid gravity value. Use a number or 'reset'.");
            return true;
        }

        // Set gravity using attribute system
        LivingEntity livingEntity = target;
        AttributeInstance gravityAttr = livingEntity.getAttribute(Attribute.GENERIC_GRAVITY);
        if (gravityAttr == null) {
            sender.sendMessage(MessageUtil.PREFIX + "§cCould not access gravity attribute.");
            return true;
        }

        gravityAttr.setBaseValue(gravityValue);

        if (target.equals(sender)) {
            MessageUtil.sendSuccess((Player) sender, "Set your gravity to §e" + gravityValue + "§a.");
        } else {
            MessageUtil.sendSuccess((Player) sender, "Set §e" + target.getName() + "§a's gravity to §e" + gravityValue + "§a.");
            MessageUtil.sendSuccess(target, "Your gravity was set to §e" + gravityValue + "§a by §e" + sender.getName() + "§a.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.addAll(Arrays.asList("0", "0.04", "0.08", "0.16", "-0.08", "reset"));
            if (sender.hasPermission("fineextras.gravity.others")) {
                completions.addAll(getOnlinePlayerNames());
            }
            return filterCompletions(completions, args[0]);
        } else if (args.length == 2 && sender.hasPermission("fineextras.gravity.others")) {
            return filterCompletions(getOnlinePlayerNames(), args[1]);
        }
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "gravity";
    }

    @Override
    public String getDescription() {
        return "Set a player's gravity (0 = no gravity, 0.08 = default, negative = reverse)";
    }

    @Override
    public String getUsage() {
        return "/gravity <value|reset> [player]";
    }

    @Override
    public String getPermission() {
        return "fineextras.gravity";
    }

    private boolean isNumericValue(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Player resolveTarget(CommandSender sender, String targetName) {
        if (!sender.hasPermission("fineextras.gravity.others")) {
            if (sender instanceof Player) {
                MessageUtil.sendError((Player) sender, "You don't have permission to change other players' gravity.");
            }
            return null;
        }
        Player target = getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(MessageUtil.PREFIX + "§cPlayer §e" + targetName + " §cis not online.");
            return null;
        }
        return target;
    }
}
