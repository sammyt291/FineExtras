package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import com.fineextras.util.TeleportRequestManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Request to teleport to another player
 * Usage: /tpa <player>
 */
public class TpaCommand extends BaseCommand {

    public TpaCommand(FineExtras plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!isPlayer(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!hasPermission(sender)) {
            MessageUtil.sendError(player, "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.PREFIX + "§cUsage: " + getUsage());
            return true;
        }

        Player target = getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.PREFIX + "§cPlayer §e" + args[0] + " §cis not online.");
            return true;
        }

        if (target.equals(player)) {
            MessageUtil.sendError(player, "You cannot send a teleport request to yourself.");
            return true;
        }

        // Create teleport request
        TeleportRequestManager manager = plugin.getTeleportRequestManager();
        boolean created = manager.createRequest(player, target, TeleportRequestManager.RequestType.TPA);

        if (!created) {
            MessageUtil.sendError(player, "You already have a pending teleport request to this player.");
            return true;
        }

        MessageUtil.sendSuccess(player, "Teleport request sent to §e" + target.getName() + "§a.");
        MessageUtil.sendTeleportRequest(target, player, true);

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(), args[0]);
        }
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "tpa";
    }

    @Override
    public String getDescription() {
        return "Request to teleport to another player";
    }

    @Override
    public String getUsage() {
        return "/tpa <player>";
    }

    @Override
    public String getPermission() {
        return "fineextras.tpa";
    }
}
