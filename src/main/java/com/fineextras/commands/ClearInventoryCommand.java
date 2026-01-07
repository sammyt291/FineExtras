package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Clear a player's inventory
 * Usage: /ci [invonly] [player]
 * invonly - Only clear main inventory (not armor or hotbar)
 */
public class ClearInventoryCommand extends BaseCommand {

    public ClearInventoryCommand(FineExtras plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            MessageUtil.sendError((Player) sender, "You don't have permission to use this command.");
            return true;
        }

        boolean invOnly = false;
        Player target = null;

        // Parse arguments
        for (String arg : args) {
            if (arg.equalsIgnoreCase("invonly")) {
                invOnly = true;
            } else if (target == null) {
                target = getPlayer(arg);
                if (target == null) {
                    sender.sendMessage(MessageUtil.PREFIX + "§cPlayer §e" + arg + " §cis not online.");
                    return true;
                }
            }
        }

        // If no target specified, use sender
        if (target == null) {
            if (!isPlayer(sender)) {
                return true;
            }
            target = (Player) sender;
        }

        // Check permission for clearing others
        if (!target.equals(sender) && !sender.hasPermission("fineextras.clearinventory.others")) {
            MessageUtil.sendError((Player) sender, "You don't have permission to clear other players' inventories.");
            return true;
        }

        // Clear inventory
        clearInventory(target, invOnly);

        // Send messages
        if (invOnly) {
            if (target.equals(sender)) {
                MessageUtil.sendSuccess((Player) sender, "Cleared your main inventory (armor and hotbar preserved).");
            } else {
                MessageUtil.sendSuccess((Player) sender, "Cleared §e" + target.getName() + "§a's main inventory (armor and hotbar preserved).");
                MessageUtil.sendSuccess(target, "Your main inventory was cleared by §e" + sender.getName() + "§a.");
            }
        } else {
            if (target.equals(sender)) {
                MessageUtil.sendSuccess((Player) sender, "Cleared your entire inventory.");
            } else {
                MessageUtil.sendSuccess((Player) sender, "Cleared §e" + target.getName() + "§a's entire inventory.");
                MessageUtil.sendSuccess(target, "Your inventory was cleared by §e" + sender.getName() + "§a.");
            }
        }

        return true;
    }

    private void clearInventory(Player player, boolean invOnly) {
        PlayerInventory inventory = player.getInventory();

        if (invOnly) {
            // Only clear slots 9-35 (main inventory, excluding hotbar 0-8 and armor)
            for (int i = 9; i <= 35; i++) {
                inventory.setItem(i, null);
            }
        } else {
            // Clear everything
            inventory.clear();
            inventory.setArmorContents(new ItemStack[4]);
            inventory.setItemInOffHand(null);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("invonly");
            if (sender.hasPermission("fineextras.clearinventory.others")) {
                completions.addAll(getOnlinePlayerNames());
            }
            return filterCompletions(completions, args[0]);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("invonly") && sender.hasPermission("fineextras.clearinventory.others")) {
                return filterCompletions(getOnlinePlayerNames(), args[1]);
            }
        }
        
        return completions;
    }

    @Override
    public String getName() {
        return "clearinventory";
    }

    @Override
    public String getDescription() {
        return "Clear your inventory or another player's inventory";
    }

    @Override
    public String getUsage() {
        return "/ci [invonly] [player]";
    }

    @Override
    public String getPermission() {
        return "fineextras.clearinventory";
    }
}
