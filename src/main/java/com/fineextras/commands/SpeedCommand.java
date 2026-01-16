package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Set player speed based on current activity
 * - Walking speed if on ground
 * - Flying speed if in creative flight
 * - Swimming speed if in water
 * Usage: /speed <value|reset>
 */
public class SpeedCommand extends BaseCommand {

    // Minecraft default speeds
    private static final float DEFAULT_WALK_SPEED = 0.2f;
    private static final float DEFAULT_FLY_SPEED = 0.1f;

    public SpeedCommand(FineExtras plugin) {
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

        if (args.length != 1) {
            sender.sendMessage(MessageUtil.PREFIX + "§cUsage: " + getUsage());
            return true;
        }

        // Check if resetting
        if (args[0].equalsIgnoreCase("reset")) {
            if (!isPlayer(sender)) {
                return true;
            }
            Player target = (Player) sender;

            // Reset both speeds
            target.setWalkSpeed(DEFAULT_WALK_SPEED);
            target.setFlySpeed(DEFAULT_FLY_SPEED);

            MessageUtil.sendSuccess((Player) sender, "Reset your speed to default.");
            return true;
        }

        // Parse speed value
        float speed;
        try {
            speed = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.PREFIX + "§cInvalid speed value. Use a number between 0 and 10, or 'reset'.");
            return true;
        }

        // Validate speed (0-10 scale, will be converted)
        if (speed < 0 || speed > 10) {
            sender.sendMessage(MessageUtil.PREFIX + "§cSpeed must be between 0 and 10.");
            return true;
        }

        if (!isPlayer(sender)) {
            return true;
        }
        Player target = (Player) sender;

        // Determine which speed to set based on player's state
        String speedType;
        
        if (target.isFlying()) {
            // Flying - set fly speed
            // Convert 0-10 scale to Minecraft's -1 to 1 scale (capped at 1)
            float flySpeed = Math.min(speed / 10.0f, 1.0f);
            target.setFlySpeed(flySpeed);
            speedType = "fly";
        } else if (target.isInWater()) {
            // Swimming - set both walk speed (affects swim speed)
            float walkSpeed = Math.min(speed / 10.0f * 2.0f, 1.0f);
            target.setWalkSpeed(walkSpeed);
            speedType = "swim";
        } else {
            // On ground - set walk speed
            // Convert 0-10 scale to Minecraft's -1 to 1 scale (default walk is 0.2)
            float walkSpeed = Math.min(speed / 10.0f * 2.0f, 1.0f);
            target.setWalkSpeed(walkSpeed);
            speedType = "walk";
        }

        MessageUtil.sendSuccess((Player) sender, "Set your " + speedType + " speed to §e" + speed + "§a.");

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "reset"));
            return filterCompletions(completions, args[0]);
        }
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "speed";
    }

    @Override
    public String getDescription() {
        return "Set your speed (walk/fly/swim based on current state)";
    }

    @Override
    public String getUsage() {
        return "/speed <0-10|reset>";
    }

    @Override
    public String getPermission() {
        return "fineextras.speed";
    }
}
