package com.fineextras.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

/**
 * Utility class for sending formatted messages
 */
public class MessageUtil {

    public static final String PREFIX = "§8[§6FineExtras§8] §r";

    /**
     * Send a prefixed message to a player
     */
    public static void sendMessage(Player player, String message) {
        player.sendMessage(PREFIX + message);
    }

    /**
     * Send an error message to a player
     */
    public static void sendError(Player player, String message) {
        player.sendMessage(PREFIX + "§c" + message);
    }

    /**
     * Send a success message to a player
     */
    public static void sendSuccess(Player player, String message) {
        player.sendMessage(PREFIX + "§a" + message);
    }

    /**
     * Send a teleport request message with clickable approve/deny buttons
     */
    public static void sendTeleportRequest(Player target, Player requester, boolean isTpa) {
        String requestType = isTpa ? "teleport to you" : "have you teleport to them";
        
        Component message = Component.text()
            .append(Component.text("[FineExtras] ", NamedTextColor.GOLD))
            .append(Component.text(requester.getName(), NamedTextColor.YELLOW))
            .append(Component.text(" wants to " + requestType + ". ", NamedTextColor.GRAY))
            .append(Component.text("[Accept]", NamedTextColor.GREEN, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/fineextras tpaccept"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to accept", NamedTextColor.GREEN))))
            .append(Component.text(" ", NamedTextColor.GRAY))
            .append(Component.text("[Deny]", NamedTextColor.RED, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/fineextras tpdeny"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to deny", NamedTextColor.RED))))
            .build();

        target.sendMessage(message);
    }

    /**
     * Send help header
     */
    public static void sendHelpHeader(Player player) {
        player.sendMessage("§6§l━━━━━━━━━━ FineExtras Help ━━━━━━━━━━");
    }

    /**
     * Send help footer
     */
    public static void sendHelpFooter(Player player) {
        player.sendMessage("§6§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    /**
     * Send a help entry
     */
    public static void sendHelpEntry(Player player, String command, String description) {
        player.sendMessage("§e" + command + " §8- §7" + description);
    }
}
