package com.shadowdefender.mc.listeners;

import com.shadowdefender.mc.ShadowDefenderMC;
import com.shadowdefender.mc.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetAddress;

public class PlayerConnectionListener implements Listener {
    
    private final ShadowDefenderMC plugin;
    
    public PlayerConnectionListener(ShadowDefenderMC plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();
        InetAddress address = event.getAddress();
        String ipAddress = address.getHostAddress();
        
        try {
            // Check rate limiting first (fastest check)
            if (!isRateLimitCheckPassed(playerName, ipAddress)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, 
                    MessageUtil.colorize(plugin.getConfigManager().getMessage("rate_limit_kick")));
                return;
            }
            
            // Check if this is a legitimate connection based on our protection rules
            if (!isConnectionAllowed(playerName, ipAddress)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, 
                    MessageUtil.colorize(plugin.getConfigManager().getMessage("bot_kick")));
                return;
            }
            
            // All checks passed - log successful connection
            plugin.getLoggingManager().logDebug(
                String.format("Player connection allowed: %s (%s)", playerName, ipAddress));
                
        } catch (Exception e) {
            plugin.getLogger().warning("Error during pre-login checks for " + playerName + ": " + e.getMessage());
            // In case of error, allow connection to avoid false positives
            plugin.getLoggingManager().logError("Pre-login check error", e);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        try {
            // Start VPN check asynchronously (non-blocking)
            plugin.getVPNManager().checkPlayerVPN(player);
            
            // Check if challenge mode is active
            if (plugin.getChallengeManager().isChallengeModeActive()) {
                // Give player a short delay before challenging (let them fully load in)
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        plugin.getChallengeManager().challengePlayer(player);
                    }
                }, 20L); // 1 second delay
            }
            
            // Log successful join
            plugin.getLoggingManager().logInfo(
                String.format("Player joined: %s (%s)", player.getName(), getPlayerIP(player)));
                
        } catch (Exception e) {
            plugin.getLogger().warning("Error during player join for " + player.getName() + ": " + e.getMessage());
            plugin.getLoggingManager().logError("Player join error", e);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        try {
            // Clean up player data
            plugin.getRateLimitManager().handlePlayerDisconnect(player);
            plugin.getChallengeManager().handlePlayerLeave(player);
            
            // Log player disconnect
            plugin.getLoggingManager().logDebug(
                String.format("Player disconnected: %s (%s)", player.getName(), getPlayerIP(player)));
                
        } catch (Exception e) {
            plugin.getLogger().warning("Error during player quit for " + player.getName() + ": " + e.getMessage());
            plugin.getLoggingManager().logError("Player quit error", e);
        }
    }
    
    private boolean isRateLimitCheckPassed(String playerName, String ipAddress) {
        // Create a temporary player-like object for rate limit checking
        // Since we're in AsyncPlayerPreLoginEvent, we don't have a Player object yet
        
        // For now, we'll do a simplified rate limit check
        // This would need to be enhanced with proper IP-based rate limiting
        
        if (!plugin.getConfigManager().isRateLimitingEnabled()) {
            return true;
        }
        
        // Basic IP-based connection throttling check
        // Note: This is simplified since we don't have a Player object yet
        // A more sophisticated implementation would track pre-login attempts
        
        return true; // Allow for now, full rate limiting happens in ProtectionManager
    }
    
    private boolean isConnectionAllowed(String playerName, String ipAddress) {
        // This is a simplified pre-check before the player actually joins
        // The main protection logic happens in ProtectionManager when the player joins
        
        // Check if plugin is enabled
        if (!plugin.getConfig().getBoolean("plugin.enabled", true)) {
            return true;
        }
        
        // Check IP whitelist (basic check)
        if (plugin.getProtectionManager().getWhitelistedIPs().contains(ipAddress)) {
            return true;
        }
        
        // Check username whitelist
        if (plugin.getConfigManager().getWhitelistUsernames().contains(playerName)) {
            return true;
        }
        
        // Check if IP is currently banned
        if (plugin.getProtectionManager().getBannedIPs().containsKey(ipAddress)) {
            Long banExpiration = plugin.getProtectionManager().getBannedIPs().get(ipAddress);
            if (banExpiration != null && System.currentTimeMillis() < banExpiration) {
                plugin.getLoggingManager().logSecurity("BLOCKED", 
                    String.format("Pre-login block - banned IP: %s (%s)", playerName, ipAddress));
                return false;
            }
        }
        
        return true; // Allow pre-login, detailed checks happen in PlayerJoinEvent
    }
    
    private String getPlayerIP(Player player) {
        InetAddress address = player.getAddress().getAddress();
        return address != null ? address.getHostAddress() : "unknown";
    }
}