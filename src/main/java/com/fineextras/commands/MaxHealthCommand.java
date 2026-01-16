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
 * Set a player's max health using the attribute system.
 * Usage: /maxhealth <value|reset> [player]
 *        /maxhealth <player> <value|reset>
 */
public class MaxHealthCommand extends BaseCommand {

    public MaxHealthCommand(FineExtras plugin) {
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

        AttributeInstance maxHealthAttr = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttr == null) {
            sender.sendMessage(MessageUtil.PREFIX + "§cCould not access max health attribute.");
            return true;
        }

        if (valueArg.equalsIgnoreCase("reset")) {
            double defaultValue = maxHealthAttr.getDefaultValue();
            maxHealthAttr.setBaseValue(defaultValue);

            if (target.equals(sender)) {
                MessageUtil.sendSuccess((Player) sender, "Reset your max health to §e" + defaultValue + "§a.");
            } else {
                MessageUtil.sendSuccess((Player) sender, "Reset §e" + target.getName() + "§a's max health to §e" + defaultValue + "§a.");
                MessageUtil.sendSuccess(target, "Your max health was reset by §e" + sender.getName() + "§a.");
            }
            return true;
        }

        double maxHealthValue;
        try {
            maxHealthValue = Double.parseDouble(valueArg);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.PREFIX + "§cInvalid max health value. Use a number or 'reset'.");
            return true;
        }

        if (maxHealthValue <= 0) {
            sender.sendMessage(MessageUtil.PREFIX + "§cMax health must be greater than 0.");
            return true;
        }

        maxHealthAttr.setBaseValue(maxHealthValue);

        if (target.equals(sender)) {
            MessageUtil.sendSuccess((Player) sender, "Set your max health to §e" + maxHealthValue + "§a.");
        } else {
            MessageUtil.sendSuccess((Player) sender, "Set §e" + target.getName() + "§a's max health to §e" + maxHealthValue + "§a.");
            MessageUtil.sendSuccess(target, "Your max health was set to §e" + maxHealthValue + "§a by §e" + sender.getName() + "§a.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.addAll(Arrays.asList("10", "20", "40", "reset"));
            if (sender.hasPermission("fineextras.maxhealth.others")) {
                completions.addAll(getOnlinePlayerNames());
            }
            return filterCompletions(completions, args[0]);
        } else if (args.length == 2 && sender.hasPermission("fineextras.maxhealth.others")) {
            return filterCompletions(getOnlinePlayerNames(), args[1]);
        }
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "maxhealth";
    }

    @Override
    public String getDescription() {
        return "Set a player's maximum health";
    }

    @Override
    public String getUsage() {
        return "/maxhealth <value|reset> [player]";
    }

    @Override
    public String getPermission() {
        return "fineextras.maxhealth";
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
        if (!sender.hasPermission("fineextras.maxhealth.others")) {
            if (sender instanceof Player) {
                MessageUtil.sendError((Player) sender, "You don't have permission to change other players' max health.");
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
