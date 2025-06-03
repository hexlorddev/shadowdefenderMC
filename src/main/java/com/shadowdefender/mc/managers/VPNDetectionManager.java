package com.shadowdefender.mc.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shadowdefender.mc.ShadowDefenderMC;
import com.shadowdefender.mc.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class VPNDetectionManager {
    
    private final ShadowDefenderMC plugin;
    
    // Cache for VPN check results
    private final ConcurrentHashMap<String, VPNCheckResult> vpnCache = new ConcurrentHashMap<>();
    
    // Statistics
    private final AtomicInteger blockedVPNsCount = new AtomicInteger(0);
    private final AtomicInteger totalChecksCount = new AtomicInteger(0);
    
    public VPNDetectionManager(ShadowDefenderMC plugin) {
        this.plugin = plugin;
    }
    
    public void checkPlayerVPN(Player player) {
        if (!plugin.getConfigManager().isVPNDetectionEnabled()) {
            return;
        }
        
        String ipAddress = getPlayerIP(player);
        
        // Check cache first
        VPNCheckResult cachedResult = vpnCache.get(ipAddress);
        if (cachedResult != null && !cachedResult.isExpired()) {
            handleVPNCheckResult(player, cachedResult);
            return;
        }
        
        // Perform async VPN check
        if (plugin.getConfigManager().isAsyncProcessingEnabled()) {
            CompletableFuture.supplyAsync(() -> performVPNCheck(ipAddress), plugin.getAsyncExecutor())
                    .thenAcceptAsync(result -> {
                        vpnCache.put(ipAddress, result);
                        handleVPNCheckResult(player, result);
                    }, plugin.getAsyncExecutor())
                    .exceptionally(throwable -> {
                        plugin.getLogger().warning("VPN check failed for " + ipAddress + ": " + throwable.getMessage());
                        return null;
                    });
        } else {
            // Synchronous check (not recommended for production)
            VPNCheckResult result = performVPNCheck(ipAddress);
            vpnCache.put(ipAddress, result);
            handleVPNCheckResult(player, result);
        }
    }
    
    private VPNCheckResult performVPNCheck(String ipAddress) {
        totalChecksCount.incrementAndGet();
        
        String apiProvider = plugin.getConfigManager().getVPNApiProvider();
        
        try {
            switch (apiProvider.toLowerCase()) {
                case "ip-api":
                    return checkWithIPAPI(ipAddress);
                case "iphub":
                    return checkWithIPHub(ipAddress);
                case "proxycheck":
                    return checkWithProxyCheck(ipAddress);
                default:
                    plugin.getLogger().warning("Unknown VPN API provider: " + apiProvider);
                    return new VPNCheckResult(false, "Unknown API provider", System.currentTimeMillis());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("VPN check failed for " + ipAddress + ": " + e.getMessage());
            return new VPNCheckResult(false, "API check failed: " + e.getMessage(), System.currentTimeMillis());
        }
    }
    
    private VPNCheckResult checkWithIPAPI(String ipAddress) throws Exception {
        String apiUrl = plugin.getConfigManager().getApiUrl("ip-api").replace("{IP}", ipAddress);
        
        HttpURLConnection connection = createConnection(apiUrl);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            
            boolean isVPN = false;
            String reason = "Clean IP";
            
            if (json.has("status") && "success".equals(json.get("status").getAsString())) {
                if (json.has("proxy") && json.get("proxy").getAsBoolean()) {
                    isVPN = true;
                    reason = "Proxy detected";
                } else if (json.has("hosting") && json.get("hosting").getAsBoolean()) {
                    isVPN = true;
                    reason = "Hosting/VPS detected";
                }
            } else {
                reason = "API query failed";
            }
            
            return new VPNCheckResult(isVPN, reason, System.currentTimeMillis());
        }
    }
    
    private VPNCheckResult checkWithIPHub(String ipAddress) throws Exception {
        String apiUrl = plugin.getConfigManager().getApiUrl("iphub").replace("{IP}", ipAddress);
        
        HttpURLConnection connection = createConnection(apiUrl);
        String apiKey = plugin.getConfigManager().getApiKey("iphub");
        if (!apiKey.isEmpty()) {
            connection.setRequestProperty("X-Key", apiKey);
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            
            boolean isVPN = false;
            String reason = "Clean IP";
            
            if (json.has("block")) {
                int blockValue = json.get("block").getAsInt();
                if (blockValue == 1) {
                    isVPN = true;
                    reason = "VPN/Proxy detected (block=1)";
                } else if (blockValue == 2) {
                    isVPN = true;
                    reason = "VPN/Proxy detected (block=2)";
                }
            }
            
            return new VPNCheckResult(isVPN, reason, System.currentTimeMillis());
        }
    }
    
    private VPNCheckResult checkWithProxyCheck(String ipAddress) throws Exception {
        String apiUrl = plugin.getConfigManager().getApiUrl("proxycheck").replace("{IP}", ipAddress);
        String apiKey = plugin.getConfigManager().getApiKey("proxycheck");
        if (!apiKey.isEmpty()) {
            apiUrl += "&key=" + apiKey;
        }
        
        HttpURLConnection connection = createConnection(apiUrl);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            
            boolean isVPN = false;
            String reason = "Clean IP";
            
            if (json.has("status") && "ok".equals(json.get("status").getAsString())) {
                if (json.has(ipAddress)) {
                    JsonObject ipData = json.getAsJsonObject(ipAddress);
                    if (ipData.has("proxy") && "yes".equals(ipData.get("proxy").getAsString())) {
                        isVPN = true;
                        reason = "Proxy detected";
                        
                        if (ipData.has("type")) {
                            reason += " (" + ipData.get("type").getAsString() + ")";
                        }
                    }
                }
            }
            
            return new VPNCheckResult(isVPN, reason, System.currentTimeMillis());
        }
    }
    
    private HttpURLConnection createConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(plugin.getConfigManager().getVPNApiTimeout());
        connection.setReadTimeout(plugin.getConfigManager().getVPNApiTimeout());
        connection.setRequestProperty("User-Agent", "ShadowDefenderMC/1.0.0");
        
        return connection;
    }
    
    private void handleVPNCheckResult(Player player, VPNCheckResult result) {
        if (!player.isOnline()) {
            return;
        }
        
        if (result.isVPN()) {
            blockedVPNsCount.incrementAndGet();
            
            String action = plugin.getConfigManager().getVPNAction().toLowerCase();
            String message = plugin.getConfigManager().getVPNKickMessage();
            
            plugin.getLoggingManager().logSecurity("VPN_DETECTED", 
                String.format("VPN/Proxy detected: %s (%s) - %s", 
                    player.getName(), getPlayerIP(player), result.getReason()));
            
            switch (action) {
                case "kick":
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (player.isOnline()) {
                            player.kickPlayer(MessageUtil.colorize(message));
                        }
                    });
                    break;
                    
                case "warn":
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (player.isOnline()) {
                            player.sendMessage(MessageUtil.colorize(plugin.getConfigManager().getPrefix() + 
                                "&eWarning: VPN/Proxy detected. Please use your real IP address."));
                        }
                    });
                    break;
                    
                case "log":
                    // Just log, already done above
                    break;
                    
                default:
                    plugin.getLogger().warning("Unknown VPN action: " + action);
                    break;
            }
        } else {
            plugin.getLoggingManager().logDebug("VPN check passed for " + getPlayerIP(player) + ": " + result.getReason());
        }
    }
    
    private String getPlayerIP(Player player) {
        InetAddress address = player.getAddress().getAddress();
        return address != null ? address.getHostAddress() : "unknown";
    }
    
    public void cleanupExpiredEntries() {
        vpnCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    public void reloadConfiguration() {
        // Clear cache to force new checks with new configuration
        vpnCache.clear();
        plugin.getLogger().info("VPN detection manager configuration reloaded");
    }
    
    // Statistics and utility methods
    
    public int getBlockedVPNsCount() {
        return blockedVPNsCount.get();
    }
    
    public int getTotalChecksCount() {
        return totalChecksCount.get();
    }
    
    public int getCachedEntriesCount() {
        return vpnCache.size();
    }
    
    public void clearCache() {
        vpnCache.clear();
        plugin.getLoggingManager().logSecurity("CACHE", "VPN cache cleared");
    }
    
    public void resetStatistics() {
        blockedVPNsCount.set(0);
        totalChecksCount.set(0);
    }
    
    // Inner class for VPN check results
    private static class VPNCheckResult {
        private final boolean isVPN;
        private final String reason;
        private final long timestamp;
        private final long expirationTime;
        
        public VPNCheckResult(boolean isVPN, String reason, long timestamp) {
            this.isVPN = isVPN;
            this.reason = reason;
            this.timestamp = timestamp;
            this.expirationTime = timestamp + (5 * 60 * 1000); // Cache for 5 minutes
        }
        
        public boolean isVPN() {
            return isVPN;
        }
        
        public String getReason() {
            return reason;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}