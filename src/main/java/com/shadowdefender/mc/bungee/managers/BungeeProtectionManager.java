package com.shadowdefender.mc.bungee.managers;

import com.shadowdefender.mc.bungee.ShadowDefenderBungee;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BungeeProtectionManager {
    
    private final ShadowDefenderBungee plugin;
    
    // IP tracking for join flood detection
    private final Map<String, List<Long>> ipJoinTimes = new ConcurrentHashMap<>();
    private final Map<String, Long> bannedIPs = new ConcurrentHashMap<>();
    
    // Statistics
    private final AtomicInteger blockedBotsCount = new AtomicInteger(0);
    
    public BungeeProtectionManager(ShadowDefenderBungee plugin) {
        this.plugin = plugin;
    }
    
    public boolean isConnectionAllowed(String ipAddress, String playerName) {
        if (!plugin.getConfigManager().isBotProtectionEnabled()) {
            return true;
        }
        
        // Check if IP is banned
        if (isIPBanned(ipAddress)) {
            plugin.getLogger().info("Blocked banned IP: " + ipAddress + " (" + playerName + ")");
            return false;
        }
        
        // Check join rate limits
        if (!isJoinRateAllowed(ipAddress)) {
            banIP(ipAddress, plugin.getConfigManager().getAutoBanDuration());
            blockedBotsCount.incrementAndGet();
            plugin.getLogger().info("Join flood detected, banned IP: " + ipAddress + " (" + playerName + ")");
            return false;
        }
        
        // Record this join
        recordJoin(ipAddress);
        
        return true;
    }
    
    private boolean isJoinRateAllowed(String ipAddress) {
        long currentTime = System.currentTimeMillis();
        List<Long> joinTimes = ipJoinTimes.computeIfAbsent(ipAddress, k -> new ArrayList<>());
        
        // Clean old entries (older than 10 seconds)
        joinTimes.removeIf(time -> currentTime - time > 10000);
        
        // Check joins in last second
        long joinsInLastSecond = joinTimes.stream()
                .mapToLong(time -> time)
                .filter(time -> currentTime - time <= 1000)
                .count();
                
        if (joinsInLastSecond >= plugin.getConfigManager().getMaxJoinsPerSecond()) {
            return false;
        }
        
        // Check joins in last 10 seconds
        if (joinTimes.size() >= plugin.getConfigManager().getMaxJoinsPer10Seconds()) {
            return false;
        }
        
        return true;
    }
    
    private void recordJoin(String ipAddress) {
        long currentTime = System.currentTimeMillis();
        List<Long> joinTimes = ipJoinTimes.computeIfAbsent(ipAddress, k -> new ArrayList<>());
        joinTimes.add(currentTime);
        
        // Limit the size of the list to prevent memory issues
        if (joinTimes.size() > 20) {
            joinTimes.remove(0);
        }
    }
    
    private boolean isIPBanned(String ipAddress) {
        Long banExpiration = bannedIPs.get(ipAddress);
        if (banExpiration == null) {
            return false;
        }
        
        if (System.currentTimeMillis() > banExpiration) {
            bannedIPs.remove(ipAddress);
            return false;
        }
        
        return true;
    }
    
    private void banIP(String ipAddress, int durationSeconds) {
        long expirationTime = System.currentTimeMillis() + (durationSeconds * 1000L);
        bannedIPs.put(ipAddress, expirationTime);
    }
    
    public void cleanupExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        
        // Clean up old join times
        ipJoinTimes.entrySet().removeIf(entry -> {
            List<Long> joinTimes = entry.getValue();
            joinTimes.removeIf(time -> currentTime - time > 10000);
            return joinTimes.isEmpty();
        });
        
        // Clean up expired bans
        bannedIPs.entrySet().removeIf(entry -> currentTime > entry.getValue());
    }
    
    public void reloadConfiguration() {
        plugin.getLogger().info("BungeeCord protection manager configuration reloaded");
    }
    
    // Statistics
    public int getBlockedBotsCount() {
        return blockedBotsCount.get();
    }
    
    public int getBannedIPsCount() {
        return bannedIPs.size();
    }
}