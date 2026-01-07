package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.scheduler.TaskScheduler;
import com.fineextras.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Teleport one player to another player
 * Usage: /tp2p <player1> <player2>
 */
public class Tp2pCommand extends BaseCommand {

    public Tp2pCommand(FineExtras plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            MessageUtil.sendError((Player) sender, "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.PREFIX + "§cUsage: " + getUsage());
            return true;
        }

        Player target1 = getPlayer(args[0]);
        Player target2 = getPlayer(args[1]);

        if (target1 == null) {
            sender.sendMessage(MessageUtil.PREFIX + "§cPlayer §e" + args[0] + " §cis not online.");
            return true;
        }

        if (target2 == null) {
            sender.sendMessage(MessageUtil.PREFIX + "§cPlayer §e" + args[1] + " §cis not online.");
            return true;
        }

        if (target1.equals(target2)) {
            sender.sendMessage(MessageUtil.PREFIX + "§cCannot teleport a player to themselves.");
            return true;
        }

        // Perform teleport using the appropriate scheduler
        if (TaskScheduler.isFolia()) {
            target1.teleportAsync(target2.getLocation()).thenAccept(success -> {
                if (success) {
                    sender.sendMessage(MessageUtil.PREFIX + "§aTeleported §e" + target1.getName() + " §ato §e" + target2.getName() + "§a.");
                    target1.sendMessage(MessageUtil.PREFIX + "§aYou have been teleported to §e" + target2.getName() + "§a.");
                } else {
                    sender.sendMessage(MessageUtil.PREFIX + "§cFailed to teleport " + target1.getName() + ".");
                }
            });
        } else {
            plugin.getScheduler().runEntityTask(target1, () -> {
                target1.teleport(target2.getLocation());
                sender.sendMessage(MessageUtil.PREFIX + "§aTeleported §e" + target1.getName() + " §ato §e" + target2.getName() + "§a.");
                target1.sendMessage(MessageUtil.PREFIX + "§aYou have been teleported to §e" + target2.getName() + "§a.");
            });
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 || args.length == 2) {
            return filterCompletions(getOnlinePlayerNames(), args[args.length - 1]);
        }
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "tp2p";
    }

    @Override
    public String getDescription() {
        return "Teleport one player to another";
    }

    @Override
    public String getUsage() {
        return "/tp2p <player1> <player2>";
    }

    @Override
    public String getPermission() {
        return "fineextras.tp2p";
    }
}
