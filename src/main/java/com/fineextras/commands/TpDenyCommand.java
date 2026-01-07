package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Deny a pending teleport request
 * Usage: /tpdeny
 */
public class TpDenyCommand extends BaseCommand {

    public TpDenyCommand(FineExtras plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!isPlayer(sender)) {
            return true;
        }

        Player player = (Player) sender;

        boolean denied = plugin.getTeleportRequestManager().denyRequest(player);
        if (!denied) {
            MessageUtil.sendError(player, "You don't have any pending teleport requests.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "tpdeny";
    }

    @Override
    public String getDescription() {
        return "Deny a pending teleport request";
    }

    @Override
    public String getUsage() {
        return "/tpdeny";
    }

    @Override
    public String getPermission() {
        return null; // No permission needed
    }
}
