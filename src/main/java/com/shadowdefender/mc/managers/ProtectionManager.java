package com.shadowdefender.mc.managers;

import com.shadowdefender.mc.ShadowDefenderMC;
import com.shadowdefender.mc.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class ProtectionManager {
    
    private final ShadowDefenderMC plugin;
    
    // IP tracking for join flood detection
    private final Map<String, List<Long>> ipJoinTimes = new ConcurrentHashMap<>();
    private final Map<String, Long> bannedIPs = new ConcurrentHashMap<>();
    private final Set<String> whitelistedIPs = ConcurrentHashMap.newKeySet();
    
    // Statistics
    private final AtomicInteger blockedBotsCount = new AtomicInteger(0);
    private final AtomicInteger suspiciousJoinsCount = new AtomicInteger(0);
    
    public ProtectionManager(ShadowDefenderMC plugin) {
        this.plugin = plugin;
        loadWhitelistedIPs();
    }
    
    private void loadWhitelistedIPs() {
        whitelistedIPs.clear();
        whitelistedIPs.add("127.0.0.1");
        whitelistedIPs.add("::1");
        
        // Load from config
        List<String> configIPs = plugin.getConfigManager().getVPNWhitelistIPs();
        for (String ip : configIPs) {
            if (ip.contains("/")) {
                // CIDR notation - for simplicity, we'll add the base IP
                String baseIP = ip.split("/")[0];
                whitelistedIPs.add(baseIP);
            } else {
                whitelistedIPs.add(ip);
            }
        }
        
        plugin.getLogger().info("Loaded " + whitelistedIPs.size() + " whitelisted IPs");
    }
    
    public boolean isPlayerAllowed(Player player) {
        String playerName = player.getName();
        String ipAddress = getPlayerIP(player);
        
        // Check if player has bypass permission
        if (player.hasPermission("shadowdefender.bypass")) {
            return true;
        }
        
        // Check IP whitelist
        if (whitelistedIPs.contains(ipAddress)) {
            return true;
        }
        
        // Check username whitelist
        if (plugin.getConfigManager().getWhitelistUsernames().contains(playerName)) {
            return true;
        }
        
        // Check if IP is banned
        if (isIPBanned(ipAddress)) {
            kickPlayer(player, plugin.getConfigManager().getMessage("bot_kick"));
            plugin.getLoggingManager().logSecurity("BLOCKED", 
                String.format("Banned IP attempt: %s (%s)", playerName, ipAddress));
            return false;
        }
        
        // Check join rate limits
        if (!isJoinRateAllowed(ipAddress)) {
            banIP(ipAddress, plugin.getConfigManager().getAutoBanDuration());
            kickPlayer(player, plugin.getConfigManager().getMessage("bot_kick"));
            blockedBotsCount.incrementAndGet();
            plugin.getLoggingManager().logSecurity("BLOCKED", 
                String.format("Join flood detected: %s (%s)", playerName, ipAddress));
            return false;
        }
        
        // Check suspicious username patterns
        if (isSuspiciousUsername(playerName)) {
            // Don't immediately ban for suspicious usernames, but log and potentially kick
            suspiciousJoinsCount.incrementAndGet();
            plugin.getLoggingManager().logSecurity("SUSPICIOUS", 
                String.format("Suspicious username: %s (%s)", playerName, ipAddress));
            
            // If we have too many suspicious joins, this might trigger challenge mode
            plugin.getChallengeManager().reportSuspiciousActivity();
            
            // For now, allow but monitor
            return true;
        }
        
        // Record this legitimate join
        recordJoin(ipAddress);
        
        return true;
    }
    
    private boolean isJoinRateAllowed(String ipAddress) {
        if (!plugin.getConfigManager().isBotProtectionEnabled()) {
            return true;
        }
        
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
    
    private boolean isSuspiciousUsername(String username) {
        List<Pattern> patterns = plugin.getConfigManager().getSuspiciousUsernamePatterns();
        
        for (Pattern pattern : patterns) {
            if (pattern.matcher(username).matches()) {
                return true;
            }
        }
        
        return false;
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
        
        plugin.getLoggingManager().logSecurity("BAN", 
            String.format("IP banned for %d seconds: %s", durationSeconds, ipAddress));
    }
    
    private void kickPlayer(Player player, String message) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) {
                player.kickPlayer(MessageUtil.colorize(message));
            }
        });
    }
    
    private String getPlayerIP(Player player) {
        InetAddress address = player.getAddress().getAddress();
        return address != null ? address.getHostAddress() : "unknown";
    }
    
    public void cleanupExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        
        // Clean up old join times (older than 10 seconds)
        ipJoinTimes.entrySet().removeIf(entry -> {
            List<Long> joinTimes = entry.getValue();
            joinTimes.removeIf(time -> currentTime - time > 10000);
            return joinTimes.isEmpty();
        });
        
        // Clean up expired bans
        bannedIPs.entrySet().removeIf(entry -> currentTime > entry.getValue());
        
        // Limit cache size to prevent memory issues
        int maxCachedIPs = plugin.getConfigManager().getMaxCachedIPs();
        if (ipJoinTimes.size() > maxCachedIPs) {
            // Remove oldest entries
            int toRemove = ipJoinTimes.size() - maxCachedIPs + 100; // Remove extra for buffer
            ipJoinTimes.entrySet().stream()
                    .sorted(Map.Entry.<String, List<Long>>comparingByValue((a, b) -> {
                        long maxA = a.stream().mapToLong(l -> l).max().orElse(0);
                        long maxB = b.stream().mapToLong(l -> l).max().orElse(0);
                        return Long.compare(maxA, maxB);
                    }))
                    .limit(toRemove)
                    .forEach(entry -> ipJoinTimes.remove(entry.getKey()));
        }
    }
    
    public void reloadConfiguration() {
        loadWhitelistedIPs();
        plugin.getLogger().info("Protection manager configuration reloaded");
    }
    
    // Utility methods for commands and other managers
    
    public void unbanIP(String ipAddress) {
        bannedIPs.remove(ipAddress);
        plugin.getLoggingManager().logSecurity("UNBAN", "IP unbanned: " + ipAddress);
    }
    
    public void clearAllBans() {
        int clearedBans = bannedIPs.size();
        bannedIPs.clear();
        plugin.getLoggingManager().logSecurity("CLEAR", "Cleared " + clearedBans + " IP bans");
    }
    
    public void addWhitelistIP(String ipAddress) {
        whitelistedIPs.add(ipAddress);
        plugin.getLoggingManager().logSecurity("WHITELIST", "IP whitelisted: " + ipAddress);
    }
    
    public void removeWhitelistIP(String ipAddress) {
        whitelistedIPs.remove(ipAddress);
        plugin.getLoggingManager().logSecurity("WHITELIST", "IP removed from whitelist: " + ipAddress);
    }
    
    // Statistics getters
    public int getBlockedBotsCount() {
        return blockedBotsCount.get();
    }
    
    public int getSuspiciousJoinsCount() {
        return suspiciousJoinsCount.get();
    }
    
    public int getBannedIPsCount() {
        return bannedIPs.size();
    }
    
    public int getTrackedIPsCount() {
        return ipJoinTimes.size();
    }
    
    public Map<String, Long> getBannedIPs() {
        return new HashMap<>(bannedIPs);
    }
    
    public Set<String> getWhitelistedIPs() {
        return new HashSet<>(whitelistedIPs);
    }
    
    public void resetStatistics() {
        blockedBotsCount.set(0);
        suspiciousJoinsCount.set(0);
    }
}