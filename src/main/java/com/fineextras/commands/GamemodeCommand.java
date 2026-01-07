package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Switch gamemode commands
 * /gmc - Creative
 * /gms - Survival
 * /gma - Adventure
 * /gmsp - Spectator
 */
public class GamemodeCommand extends BaseCommand {

    private final String gamemodeName;
    private final GameMode gameMode;

    public GamemodeCommand(FineExtras plugin, String gamemodeName) {
        super(plugin);
        this.gamemodeName = gamemodeName;
        this.gameMode = parseGameMode(gamemodeName);
    }

    private GameMode parseGameMode(String name) {
        return switch (name.toLowerCase()) {
            case "creative" -> GameMode.CREATIVE;
            case "survival" -> GameMode.SURVIVAL;
            case "adventure" -> GameMode.ADVENTURE;
            case "spectator" -> GameMode.SPECTATOR;
            default -> GameMode.SURVIVAL;
        };
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            MessageUtil.sendError((Player) sender, "You don't have permission to use this command.");
            return true;
        }

        Player target;

        if (args.length > 0) {
            // Check permission for changing others' gamemode
            if (!sender.hasPermission("fineextras.gamemode.others")) {
                MessageUtil.sendError((Player) sender, "You don't have permission to change other players' gamemodes.");
                return true;
            }

            target = getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(MessageUtil.PREFIX + "§cPlayer §e" + args[0] + " §cis not online.");
                return true;
            }
        } else {
            if (!isPlayer(sender)) {
                return true;
            }
            target = (Player) sender;
        }

        // Set gamemode
        target.setGameMode(gameMode);

        // Format gamemode name nicely
        String formattedGamemode = gamemodeName.substring(0, 1).toUpperCase() + gamemodeName.substring(1);

        if (target.equals(sender)) {
            MessageUtil.sendSuccess((Player) sender, "Set your gamemode to §e" + formattedGamemode + "§a.");
        } else {
            MessageUtil.sendSuccess((Player) sender, "Set §e" + target.getName() + "§a's gamemode to §e" + formattedGamemode + "§a.");
            MessageUtil.sendSuccess(target, "Your gamemode was set to §e" + formattedGamemode + "§a by §e" + sender.getName() + "§a.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("fineextras.gamemode.others")) {
            return filterCompletions(getOnlinePlayerNames(), args[0]);
        }
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return switch (gamemodeName.toLowerCase()) {
            case "creative" -> "gmc";
            case "survival" -> "gms";
            case "adventure" -> "gma";
            case "spectator" -> "gmsp";
            default -> "gm";
        };
    }

    @Override
    public String getDescription() {
        String formattedGamemode = gamemodeName.substring(0, 1).toUpperCase() + gamemodeName.substring(1);
        return "Set gamemode to " + formattedGamemode;
    }

    @Override
    public String getUsage() {
        return "/" + getName() + " [player]";
    }

    @Override
    public String getPermission() {
        return "fineextras.gamemode." + gamemodeName;
    }
}
