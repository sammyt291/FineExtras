package com.fineextras.commands;

import com.fineextras.FineExtras;
import com.fineextras.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Main FineExtras command handler
 * All plugin commands are also available as subcommands of /fineextras
 * Usage: /fineextras <subcommand> [args...]
 * Aliases: /fe
 */
public class FineExtrasCommand implements CommandExecutor, TabCompleter {

    private final FineExtras plugin;
    private final Map<String, BaseCommand> subCommands = new LinkedHashMap<>();

    public FineExtrasCommand(FineExtras plugin) {
        this.plugin = plugin;
        registerSubCommands();
    }

    private void registerSubCommands() {
        // Register all subcommands
        subCommands.put("help", null); // Special case - handled separately
        subCommands.put("tp2p", new Tp2pCommand(plugin));
        subCommands.put("ci", new ClearInventoryCommand(plugin));
        subCommands.put("clearinventory", new ClearInventoryCommand(plugin));
        subCommands.put("gmc", new GamemodeCommand(plugin, "creative"));
        subCommands.put("gms", new GamemodeCommand(plugin, "survival"));
        subCommands.put("gma", new GamemodeCommand(plugin, "adventure"));
        subCommands.put("gmsp", new GamemodeCommand(plugin, "spectator"));
        subCommands.put("tpa", new TpaCommand(plugin));
        subCommands.put("tpr", new TprCommand(plugin));
        subCommands.put("tpaccept", new TpAcceptCommand(plugin));
        subCommands.put("tpdeny", new TpDenyCommand(plugin));
        subCommands.put("speed", new SpeedCommand(plugin));
        subCommands.put("gravity", new GravityCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            showHelp(sender, args.length > 1 ? args[1] : null);
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        BaseCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            sender.sendMessage(MessageUtil.PREFIX + "§cUnknown subcommand: §e" + subCommandName);
            sender.sendMessage(MessageUtil.PREFIX + "§7Use §e/fineextras help §7for a list of commands.");
            return true;
        }

        // Remove the subcommand from args
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return subCommand.execute(sender, subArgs);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            // Complete subcommand names
            List<String> completions = new ArrayList<>(subCommands.keySet());
            return filterCompletions(completions, args[0]);
        } else if (args.length > 1) {
            // Delegate to subcommand
            String subCommandName = args[0].toLowerCase();
            BaseCommand subCommand = subCommands.get(subCommandName);
            if (subCommand != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subCommand.tabComplete(sender, subArgs);
            }
        }
        return new ArrayList<>();
    }

    private void showHelp(CommandSender sender, String page) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("FineExtras Commands:");
            for (Map.Entry<String, BaseCommand> entry : subCommands.entrySet()) {
                if (entry.getValue() != null) {
                    sender.sendMessage("  /fe " + entry.getKey() + " - " + entry.getValue().getDescription());
                }
            }
            return;
        }

        MessageUtil.sendHelpHeader(player);
        player.sendMessage("");
        player.sendMessage("§7All commands can be used via §e/fineextras §7or §e/fe");
        player.sendMessage("§7Direct commands are only available if not");
        player.sendMessage("§7registered by another plugin.");
        player.sendMessage("");

        // Teleportation commands
        player.sendMessage("§6§lTeleportation:");
        MessageUtil.sendHelpEntry(player, "/fe tp2p <player1> <player2>", "Teleport player1 to player2");
        MessageUtil.sendHelpEntry(player, "/fe tpa <player>", "Request to teleport to a player");
        MessageUtil.sendHelpEntry(player, "/fe tpr <player>", "Request a player to teleport to you");
        MessageUtil.sendHelpEntry(player, "/fe tpaccept", "Accept a teleport request");
        MessageUtil.sendHelpEntry(player, "/fe tpdeny", "Deny a teleport request");
        player.sendMessage("");

        // Inventory commands
        player.sendMessage("§6§lInventory:");
        MessageUtil.sendHelpEntry(player, "/fe ci [invonly] [player]", "Clear inventory");
        MessageUtil.sendHelpEntry(player, "/fe clearinventory [invonly] [player]", "Clear inventory (alias)");
        player.sendMessage("  §7Use §einvonly §7to keep armor and hotbar");
        player.sendMessage("");

        // Gamemode commands
        player.sendMessage("§6§lGamemode:");
        MessageUtil.sendHelpEntry(player, "/fe gmc [player]", "Set gamemode to Creative");
        MessageUtil.sendHelpEntry(player, "/fe gms [player]", "Set gamemode to Survival");
        MessageUtil.sendHelpEntry(player, "/fe gma [player]", "Set gamemode to Adventure");
        MessageUtil.sendHelpEntry(player, "/fe gmsp [player]", "Set gamemode to Spectator");
        player.sendMessage("");

        // Movement commands
        player.sendMessage("§6§lMovement:");
        MessageUtil.sendHelpEntry(player, "/fe speed <0-10|reset> [player]", "Set movement speed");
        player.sendMessage("  §7Speed type depends on state: flying, swimming, or walking");
        MessageUtil.sendHelpEntry(player, "/fe gravity <value|reset> [player]", "Set player gravity");
        player.sendMessage("  §7Use §e0 §7to disable gravity, any other value enables it");
        player.sendMessage("");

        MessageUtil.sendHelpFooter(player);

        // Show which direct commands are available
        Set<String> registered = plugin.getRegisteredCommands();
        if (!registered.isEmpty()) {
            player.sendMessage("");
            player.sendMessage("§7Direct commands available: §e/" + String.join("§7, §e/", registered));
        }
    }

    private List<String> filterCompletions(List<String> completions, String current) {
        if (current == null || current.isEmpty()) {
            return completions;
        }
        String lowerCurrent = current.toLowerCase();
        List<String> filtered = new ArrayList<>();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(lowerCurrent)) {
                filtered.add(completion);
            }
        }
        return filtered;
    }
}
