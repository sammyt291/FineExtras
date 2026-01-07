package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Set player walk/sprint speed
 * Usage: /walkspeed <value|reset> [player]
 * 
 * Uses Bukkit's setWalkSpeed() method
 * - Scale: 0-10 (converted to Minecraft's -1 to 1 internally)
 * - Default walk speed is 2 (0.2 in Minecraft scale)
 * - This affects both walking and sprinting speed
 */
public class WalkSpeedCommand extends BaseCommand {

    private static final float DEFAULT_WALK_SPEED = 0.2f;

    public WalkSpeedCommand(FineExtras plugin) {
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
                if (!sender.hasPermission("fineextras.walkspeed.others")) {
                    if (sender instanceof Player) {
                        MessageUtil.sendError((Player) sender, "You don't have permission to change other players' walk speed.");
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

            // Reset walk speed to default
            target.setWalkSpeed(DEFAULT_WALK_SPEED);

            if (target.equals(sender)) {
                MessageUtil.sendSuccess((Player) sender, "Reset your walk speed to default.");
            } else {
                MessageUtil.sendSuccess((Player) sender, "Reset §e" + target.getName() + "§a's walk speed to default.");
                MessageUtil.sendSuccess(target, "Your walk speed was reset by §e" + sender.getName() + "§a.");
            }
            return true;
        }

        // Parse speed value
        float speed;
        try {
            speed = Float.parseFloat(valueArg);
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
            if (!sender.hasPermission("fineextras.walkspeed.others")) {
                if (sender instanceof Player) {
                    MessageUtil.sendError((Player) sender, "You don't have permission to change other players' walk speed.");
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

        // Convert 0-10 scale to Minecraft's -1 to 1 scale
        // Default walk speed is 0.2, so scale accordingly (multiply by 0.2 to match default at 1)
        // But we want 10 to be max speed, so we use /10 then *2 to scale properly
        float walkSpeed = Math.min(speed / 10.0f * 2.0f, 1.0f);
        target.setWalkSpeed(walkSpeed);

        if (target.equals(sender)) {
            MessageUtil.sendSuccess((Player) sender, "Set your walk speed to §e" + speed + "§a.");
        } else {
            MessageUtil.sendSuccess((Player) sender, "Set §e" + target.getName() + "§a's walk speed to §e" + speed + "§a.");
            MessageUtil.sendSuccess(target, "Your walk speed was set to §e" + speed + "§a by §e" + sender.getName() + "§a.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "reset"));
            return filterCompletions(completions, args[0]);
        } else if (args.length == 2 && sender.hasPermission("fineextras.walkspeed.others")) {
            return filterCompletions(getOnlinePlayerNames(), args[1]);
        }
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "walkspeed";
    }

    @Override
    public String getDescription() {
        return "Set your walk/sprint speed";
    }

    @Override
    public String getUsage() {
        return "/walkspeed <0-10|reset> [player]";
    }

    @Override
    public String getPermission() {
        return "fineextras.walkspeed";
    }
}
