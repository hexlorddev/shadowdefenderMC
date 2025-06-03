package com.shadowdefender.mc.managers;

import com.shadowdefender.mc.ShadowDefenderMC;
import com.shadowdefender.mc.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ChallengeManager {
    
    private final ShadowDefenderMC plugin;
    
    // Challenge system state
    private boolean challengeModeActive = false;
    private long challengeModeActivatedAt = 0;
    private final AtomicInteger suspiciousActivityCount = new AtomicInteger(0);
    
    // Player verification tracking
    private final Map<UUID, PlayerChallenge> playerChallenges = new ConcurrentHashMap<>();
    private final Set<UUID> verifiedPlayers = ConcurrentHashMap.newKeySet();
    
    // Original locations for teleporting back
    private final Map<UUID, Location> originalLocations = new ConcurrentHashMap<>();
    
    public ChallengeManager(ShadowDefenderMC plugin) {
        this.plugin = plugin;
        
        // Schedule verification timeout checks
        plugin.getScheduledExecutor().scheduleAtFixedRate(this::checkVerificationTimeouts, 10, 10, 
                java.util.concurrent.TimeUnit.SECONDS);
    }
    
    public void reportSuspiciousActivity() {
        if (!plugin.getConfigManager().isChallengeModeEnabled()) {
            return;
        }
        
        int count = suspiciousActivityCount.incrementAndGet();
        int threshold = plugin.getConfigManager().getChallengeTriggerThreshold();
        
        if (count >= threshold && !challengeModeActive) {
            activateChallengeMode();
        }
    }
    
    private void activateChallengeMode() {
        challengeModeActive = true;
        challengeModeActivatedAt = System.currentTimeMillis();
        
        plugin.getLoggingManager().logSecurity("CHALLENGE_ACTIVATED", 
            "Challenge mode activated due to suspicious activity threshold reached");
        
        // Broadcast to admins
        String message = MessageUtil.colorize(plugin.getConfigManager().getPrefix() + 
            plugin.getConfigManager().getMessage("challenge_activated"));
            
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("shadowdefender.admin")) {
                player.sendMessage(message);
            }
        });
        
        // Challenge all currently unverified players
        Bukkit.getOnlinePlayers().forEach(this::challengePlayer);
    }
    
    private void deactivateChallengeMode() {
        challengeModeActive = false;
        challengeModeActivatedAt = 0;
        suspiciousActivityCount.set(0);
        
        plugin.getLoggingManager().logSecurity("CHALLENGE_DEACTIVATED", 
            "Challenge mode deactivated");
        
        // Teleport all players back to their original locations
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location originalLocation = originalLocations.remove(player.getUniqueId());
            if (originalLocation != null) {
                player.teleport(originalLocation);
            }
        }
        
        // Clear challenge data
        playerChallenges.clear();
    }
    
    public void challengePlayer(Player player) {
        if (!challengeModeActive || player.hasPermission("shadowdefender.bypass")) {
            return;
        }
        
        UUID playerId = player.getUniqueId();
        
        // Skip if already verified
        if (verifiedPlayers.contains(playerId)) {
            return;
        }
        
        // Generate verification code
        String verificationCode = generateVerificationCode();
        long timeout = System.currentTimeMillis() + (plugin.getConfigManager().getVerificationTimeout() * 1000L);
        
        PlayerChallenge challenge = new PlayerChallenge(verificationCode, timeout);
        playerChallenges.put(playerId, challenge);
        
        // Store original location
        originalLocations.put(playerId, player.getLocation());
        
        // Teleport to limbo world if it exists
        teleportToLimbo(player);
        
        // Send challenge message
        String welcomeMessage = plugin.getConfigManager().getChallengeWelcomeMessage()
                .replace("{CODE}", verificationCode);
        player.sendMessage(MessageUtil.colorize(welcomeMessage));
        
        plugin.getLoggingManager().logSecurity("CHALLENGE_ISSUED", 
            String.format("Challenge issued to player: %s (code: %s)", player.getName(), verificationCode));
    }
    
    private void teleportToLimbo(Player player) {
        String limboWorldName = plugin.getConfigManager().getLimboWorld();
        World limboWorld = Bukkit.getWorld(limboWorldName);
        
        if (limboWorld != null) {
            // Find a safe spawn location in limbo world
            Location limboSpawn = limboWorld.getSpawnLocation();
            player.teleport(limboSpawn);
        } else {
            // If limbo world doesn't exist, teleport to a high location in current world
            Location highLocation = player.getLocation().clone();
            highLocation.setY(300); // High enough to be isolated
            player.teleport(highLocation);
        }
    }
    
    public boolean verifyPlayer(Player player, String inputCode) {
        UUID playerId = player.getUniqueId();
        PlayerChallenge challenge = playerChallenges.get(playerId);
        
        if (challenge == null) {
            return false; // No challenge for this player
        }
        
        if (challenge.isExpired()) {
            // Challenge expired
            playerChallenges.remove(playerId);
            kickPlayerForTimeout(player);
            return false;
        }
        
        if (!challenge.getVerificationCode().equals(inputCode)) {
            // Wrong code
            challenge.incrementAttempts();
            if (challenge.getAttempts() >= 3) {
                // Too many failed attempts
                playerChallenges.remove(playerId);
                kickPlayerForTimeout(player);
                return false;
            }
            return false;
        }
        
        // Successful verification
        playerChallenges.remove(playerId);
        verifiedPlayers.add(playerId);
        
        // Teleport back to original location
        Location originalLocation = originalLocations.remove(playerId);
        if (originalLocation != null) {
            player.teleport(originalLocation);
        }
        
        String successMessage = plugin.getConfigManager().getMessage("verification_success");
        player.sendMessage(MessageUtil.colorize(plugin.getConfigManager().getPrefix() + successMessage));
        
        plugin.getLoggingManager().logSecurity("CHALLENGE_VERIFIED", 
            String.format("Player successfully verified: %s", player.getName()));
        
        return true;
    }
    
    private void kickPlayerForTimeout(Player player) {
        String kickMessage = plugin.getConfigManager().getChallengeKickMessage();
        player.kickPlayer(MessageUtil.colorize(kickMessage));
        
        plugin.getLoggingManager().logSecurity("CHALLENGE_TIMEOUT", 
            String.format("Player kicked for verification timeout: %s", player.getName()));
    }
    
    private void checkVerificationTimeouts() {
        long currentTime = System.currentTimeMillis();
        
        // Check if challenge mode should be deactivated
        if (challengeModeActive) {
            long challengeDuration = plugin.getConfigManager().getChallengeDuration() * 1000L;
            if (currentTime - challengeModeActivatedAt > challengeDuration) {
                deactivateChallengeMode();
                return;
            }
        }
        
        // Check for expired verifications
        Iterator<Map.Entry<UUID, PlayerChallenge>> iterator = playerChallenges.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PlayerChallenge> entry = iterator.next();
            UUID playerId = entry.getKey();
            PlayerChallenge challenge = entry.getValue();
            
            if (challenge.isExpired()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    kickPlayerForTimeout(player);
                }
                iterator.remove();
                originalLocations.remove(playerId);
            }
        }
    }
    
    public void cleanupExpiredChallenges() {
        checkVerificationTimeouts();
        
        // Reset suspicious activity count periodically
        if (!challengeModeActive && suspiciousActivityCount.get() > 0) {
            // Decay suspicious activity count over time
            int currentCount = suspiciousActivityCount.get();
            int newCount = Math.max(0, currentCount - 1);
            suspiciousActivityCount.set(newCount);
        }
    }
    
    private String generateVerificationCode() {
        // Generate a 4-digit numeric code
        return String.format("%04d", ThreadLocalRandom.current().nextInt(1000, 10000));
    }
    
    public void handlePlayerLeave(Player player) {
        UUID playerId = player.getUniqueId();
        playerChallenges.remove(playerId);
        verifiedPlayers.remove(playerId);
        originalLocations.remove(playerId);
    }
    
    public void reloadConfiguration() {
        // Reset challenge mode on configuration reload
        if (challengeModeActive) {
            deactivateChallengeMode();
        }
        plugin.getLogger().info("Challenge manager configuration reloaded");
    }
    
    // Status and utility methods
    
    public boolean isChallengeModeActive() {
        return challengeModeActive;
    }
    
    public int getPendingChallenges() {
        return playerChallenges.size();
    }
    
    public int getVerifiedPlayersCount() {
        return verifiedPlayers.size();
    }
    
    public int getSuspiciousActivityCount() {
        return suspiciousActivityCount.get();
    }
    
    public boolean isPlayerVerified(Player player) {
        return verifiedPlayers.contains(player.getUniqueId());
    }
    
    public boolean isPlayerChallenged(Player player) {
        return playerChallenges.containsKey(player.getUniqueId());
    }
    
    public void forceActivateChallengeMode() {
        activateChallengeMode();
    }
    
    public void forceDeactivateChallengeMode() {
        deactivateChallengeMode();
    }
    
    public void resetSuspiciousActivityCount() {
        suspiciousActivityCount.set(0);
    }
    
    // Inner class for player challenge data
    private static class PlayerChallenge {
        private final String verificationCode;
        private final long expirationTime;
        private int attempts;
        
        public PlayerChallenge(String verificationCode, long expirationTime) {
            this.verificationCode = verificationCode;
            this.expirationTime = expirationTime;
            this.attempts = 0;
        }
        
        public String getVerificationCode() {
            return verificationCode;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
        
        public int getAttempts() {
            return attempts;
        }
        
        public void incrementAttempts() {
            attempts++;
        }
    }
}