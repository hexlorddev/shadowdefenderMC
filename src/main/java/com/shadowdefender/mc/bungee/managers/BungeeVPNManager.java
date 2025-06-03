package com.shadowdefender.mc.bungee.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shadowdefender.mc.bungee.ShadowDefenderBungee;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BungeeVPNManager {
    
    private final ShadowDefenderBungee plugin;
    
    // Cache for VPN check results
    private final ConcurrentHashMap<String, VPNResult> vpnCache = new ConcurrentHashMap<>();
    
    // Statistics
    private final AtomicInteger blockedVPNsCount = new AtomicInteger(0);
    
    public BungeeVPNManager(ShadowDefenderBungee plugin) {
        this.plugin = plugin;
    }
    
    public boolean isVPNAllowed(String ipAddress) {
        if (!plugin.getConfigManager().isVPNDetectionEnabled()) {
            return true;
        }
        
        // Check cache first
        VPNResult cachedResult = vpnCache.get(ipAddress);
        if (cachedResult != null && !cachedResult.isExpired()) {
            if (cachedResult.isVPN()) {
                blockedVPNsCount.incrementAndGet();
                plugin.getLogger().info("Blocked VPN/Proxy: " + ipAddress + " - " + cachedResult.getReason());
                return false;
            }
            return true;
        }
        
        // Perform synchronous check (simplified for BungeeCord)
        try {
            VPNResult result = performVPNCheck(ipAddress);
            vpnCache.put(ipAddress, result);
            
            if (result.isVPN()) {
                blockedVPNsCount.incrementAndGet();
                plugin.getLogger().info("Blocked VPN/Proxy: " + ipAddress + " - " + result.getReason());
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("VPN check failed for " + ipAddress + ", allowing connection: " + e.getMessage());
            return true; // Allow on error to prevent false positives
        }
    }
    
    private VPNResult performVPNCheck(String ipAddress) throws Exception {
        String apiProvider = plugin.getConfigManager().getVPNApiProvider();
        
        if ("ip-api".equals(apiProvider)) {
            return checkWithIPAPI(ipAddress);
        } else {
            plugin.getLogger().warning("Unknown VPN API provider: " + apiProvider);
            return new VPNResult(false, "Unknown API provider");
        }
    }
    
    private VPNResult checkWithIPAPI(String ipAddress) throws Exception {
        String apiUrl = "http://ip-api.com/json/" + ipAddress + "?fields=status,message,proxy,hosting,query";
        
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(plugin.getConfigManager().getVPNApiTimeout());
        connection.setReadTimeout(plugin.getConfigManager().getVPNApiTimeout());
        connection.setRequestProperty("User-Agent", "ShadowDefenderMC-Bungee/1.0.0");
        
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
            
            return new VPNResult(isVPN, reason);
        }
    }
    
    public void reloadConfiguration() {
        vpnCache.clear(); // Clear cache on reload
        plugin.getLogger().info("BungeeCord VPN manager configuration reloaded");
    }
    
    // Statistics
    public int getBlockedVPNsCount() {
        return blockedVPNsCount.get();
    }
    
    // Inner class for VPN results
    private static class VPNResult {
        private final boolean isVPN;
        private final String reason;
        private final long timestamp;
        
        public VPNResult(boolean isVPN, String reason) {
            this.isVPN = isVPN;
            this.reason = reason;
            this.timestamp = System.currentTimeMillis();
        }
        
        public boolean isVPN() {
            return isVPN;
        }
        
        public String getReason() {
            return reason;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 300000; // 5 minutes
        }
    }
}