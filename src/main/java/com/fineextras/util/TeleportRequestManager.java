package com.fineextras.util;

import com.fineextras.FineExtras;
import com.fineextras.scheduler.TaskScheduler;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages teleport requests between players
 */
public class TeleportRequestManager {

    private final FineExtras plugin;
    private final Map<UUID, TeleportRequest> requests = new ConcurrentHashMap<>();
    
    // Request timeout in ticks (60 seconds = 1200 ticks)
    private static final long REQUEST_TIMEOUT_TICKS = 1200L;

    public TeleportRequestManager(FineExtras plugin) {
        this.plugin = plugin;
    }

    /**
     * Create a new teleport request
     * @param requester The player making the request
     * @param target The target player
     * @param type The type of request (TPA = requester goes to target, TPR = target comes to requester)
     * @return true if request was created, false if one already exists
     */
    public boolean createRequest(Player requester, Player target, RequestType type) {
        UUID targetId = target.getUniqueId();
        
        // Remove any existing request to this target
        TeleportRequest existing = requests.get(targetId);
        if (existing != null && existing.getRequester().equals(requester.getUniqueId())) {
            return false; // Already has a pending request from this player
        }

        TeleportRequest request = new TeleportRequest(requester.getUniqueId(), targetId, type);
        requests.put(targetId, request);

        // Schedule expiration
        plugin.getScheduler().runTaskLater(() -> {
            TeleportRequest current = requests.get(targetId);
            if (current != null && current.equals(request)) {
                requests.remove(targetId);
                Player req = plugin.getServer().getPlayer(requester.getUniqueId());
                Player tgt = plugin.getServer().getPlayer(targetId);
                if (req != null) {
                    req.sendMessage("§cYour teleport request to §e" + (tgt != null ? tgt.getName() : "player") + "§c has expired.");
                }
            }
        }, REQUEST_TIMEOUT_TICKS);

        return true;
    }

    /**
     * Get the pending request for a target player
     */
    public TeleportRequest getRequest(UUID targetId) {
        return requests.get(targetId);
    }

    /**
     * Accept a teleport request
     */
    public boolean acceptRequest(Player target) {
        TeleportRequest request = requests.remove(target.getUniqueId());
        if (request == null) {
            return false;
        }

        Player requester = plugin.getServer().getPlayer(request.getRequester());
        if (requester == null || !requester.isOnline()) {
            target.sendMessage("§cThe player who sent the request is no longer online.");
            return false;
        }

        // Execute teleport based on request type
        if (request.getType() == RequestType.TPA) {
            // Requester teleports to target
            teleportPlayer(requester, target);
            requester.sendMessage("§aTeleporting to §e" + target.getName() + "§a...");
            target.sendMessage("§e" + requester.getName() + "§a is teleporting to you.");
        } else {
            // Target teleports to requester (TPR)
            teleportPlayer(target, requester);
            target.sendMessage("§aTeleporting to §e" + requester.getName() + "§a...");
            requester.sendMessage("§e" + target.getName() + "§a is teleporting to you.");
        }

        return true;
    }

    /**
     * Deny a teleport request
     */
    public boolean denyRequest(Player target) {
        TeleportRequest request = requests.remove(target.getUniqueId());
        if (request == null) {
            return false;
        }

        Player requester = plugin.getServer().getPlayer(request.getRequester());
        if (requester != null && requester.isOnline()) {
            requester.sendMessage("§c" + target.getName() + " denied your teleport request.");
        }
        target.sendMessage("§cTeleport request denied.");

        return true;
    }

    /**
     * Clear all pending requests
     */
    public void clearAllRequests() {
        requests.clear();
    }

    /**
     * Teleport a player to another player using the appropriate scheduler
     */
    private void teleportPlayer(Player from, Player to) {
        if (TaskScheduler.isFolia()) {
            // On Folia, use the entity scheduler for teleportation
            from.teleportAsync(to.getLocation());
        } else {
            plugin.getScheduler().runEntityTask(from, () -> from.teleport(to.getLocation()));
        }
    }

    public enum RequestType {
        TPA,  // Requester wants to go to target
        TPR   // Requester wants target to come to them
    }

    public static class TeleportRequest {
        private final UUID requester;
        private final UUID target;
        private final RequestType type;
        private final long timestamp;

        public TeleportRequest(UUID requester, UUID target, RequestType type) {
            this.requester = requester;
            this.target = target;
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }

        public UUID getRequester() {
            return requester;
        }

        public UUID getTarget() {
            return target;
        }

        public RequestType getType() {
            return type;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
