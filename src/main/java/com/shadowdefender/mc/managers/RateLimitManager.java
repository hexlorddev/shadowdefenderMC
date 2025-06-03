package com.shadowdefender.mc.managers;

import com.shadowdefender.mc.ShadowDefenderMC;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimitManager {
    
    private final ShadowDefenderMC plugin;
    
    // Connection tracking
    private final ConcurrentHashMap<String, ConnectionData> connectionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastConnectionTime = new ConcurrentHashMap<>();
    
    // Packet rate limiting (if packet filtering is available)
    private final ConcurrentHashMap<String, PacketRateData> packetRateMap = new ConcurrentHashMap<>();
    
    // Statistics
    private final AtomicInteger blockedConnectionsCount = new AtomicInteger(0);
    private final AtomicInteger totalConnectionsCount = new AtomicInteger(0);
    
    public RateLimitManager(ShadowDefenderMC plugin) {
        this.plugin = plugin;
    }
    
    public boolean isConnectionAllowed(Player player) {
        if (!plugin.getConfigManager().isRateLimitingEnabled()) {
            return true;
        }
        
        String ipAddress = getPlayerIP(player);
        long currentTime = System.currentTimeMillis();
        
        totalConnectionsCount.incrementAndGet();
        
        // Check connection throttling
        if (!isConnectionThrottleAllowed(ipAddress, currentTime)) {
            blockedConnectionsCount.incrementAndGet();
            plugin.getLoggingManager().logSecurity("RATE_LIMIT", 
                String.format("Connection throttled: %s (%s)", player.getName(), ipAddress));
            return false;
        }
        
        // Check max connections per IP
        if (!isMaxConnectionsAllowed(ipAddress)) {
            blockedConnectionsCount.incrementAndGet();
            plugin.getLoggingManager().logSecurity("RATE_LIMIT", 
                String.format("Max connections exceeded: %s (%s)", player.getName(), ipAddress));
            return false;
        }
        
        // Record this connection
        recordConnection(ipAddress, currentTime);
        
        return true;
    }
    
    private boolean isConnectionThrottleAllowed(String ipAddress, long currentTime) {
        Long lastConnection = lastConnectionTime.get(ipAddress);
        if (lastConnection == null) {
            return true;
        }
        
        long timeDiff = currentTime - lastConnection;
        int throttleTime = plugin.getConfigManager().getConnectionThrottle();
        
        return timeDiff >= throttleTime;
    }
    
    private boolean isMaxConnectionsAllowed(String ipAddress) {
        ConnectionData connectionData = connectionMap.get(ipAddress);
        if (connectionData == null) {
            return true;
        }
        
        int maxConnections = plugin.getConfigManager().getMaxConnectionsPerIP();
        return connectionData.getActiveConnections() < maxConnections;
    }
    
    private void recordConnection(String ipAddress, long currentTime) {
        lastConnectionTime.put(ipAddress, currentTime);
        
        ConnectionData connectionData = connectionMap.computeIfAbsent(ipAddress, 
            k -> new ConnectionData());
        connectionData.incrementConnections();
    }
    
    public void handlePlayerDisconnect(Player player) {
        String ipAddress = getPlayerIP(player);
        ConnectionData connectionData = connectionMap.get(ipAddress);
        
        if (connectionData != null) {
            connectionData.decrementConnections();
            if (connectionData.getActiveConnections() <= 0) {
                connectionMap.remove(ipAddress);
            }
        }
    }
    
    // Packet rate limiting methods (for future enhancement)
    
    public boolean isPacketRateAllowed(String ipAddress) {
        if (!plugin.getConfigManager().isRateLimitingEnabled()) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        PacketRateData rateData = packetRateMap.computeIfAbsent(ipAddress, 
            k -> new PacketRateData());
        
        return rateData.isPacketAllowed(currentTime, plugin.getConfigManager().getMaxPacketsPerSecond());
    }
    
    public void recordPacket(String ipAddress) {
        PacketRateData rateData = packetRateMap.computeIfAbsent(ipAddress, 
            k -> new PacketRateData());
        rateData.recordPacket();
    }
    
    private String getPlayerIP(Player player) {
        InetAddress address = player.getAddress().getAddress();
        return address != null ? address.getHostAddress() : "unknown";
    }
    
    public void cleanupExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        
        // Clean up old connection throttle data (older than 1 minute)
        lastConnectionTime.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > 60000);
        
        // Clean up packet rate data
        packetRateMap.entrySet().removeIf(entry -> 
            entry.getValue().isExpired(currentTime));
    }
    
    public void reloadConfiguration() {
        plugin.getLogger().info("Rate limit manager configuration reloaded");
    }
    
    // Statistics and utility methods
    
    public int getActiveConnectionsCount() {
        return connectionMap.values().stream()
                .mapToInt(ConnectionData::getActiveConnections)
                .sum();
    }
    
    public int getBlockedConnectionsCount() {
        return blockedConnectionsCount.get();
    }
    
    public int getTotalConnectionsCount() {
        return totalConnectionsCount.get();
    }
    
    public int getTrackedIPsCount() {
        return connectionMap.size();
    }
    
    public void resetStatistics() {
        blockedConnectionsCount.set(0);
        totalConnectionsCount.set(0);
    }
    
    // Inner classes for tracking data
    
    private static class ConnectionData {
        private final AtomicInteger activeConnections = new AtomicInteger(0);
        
        public void incrementConnections() {
            activeConnections.incrementAndGet();
        }
        
        public void decrementConnections() {
            activeConnections.decrementAndGet();
        }
        
        public int getActiveConnections() {
            return Math.max(0, activeConnections.get());
        }
    }
    
    private static class PacketRateData {
        private final AtomicInteger packetCount = new AtomicInteger(0);
        private final AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());
        
        public boolean isPacketAllowed(long currentTime, int maxPacketsPerSecond) {
            // Reset counter every second
            if (currentTime - lastResetTime.get() >= 1000) {
                packetCount.set(0);
                lastResetTime.set(currentTime);
            }
            
            return packetCount.get() < maxPacketsPerSecond;
        }
        
        public void recordPacket() {
            packetCount.incrementAndGet();
        }
        
        public boolean isExpired(long currentTime) {
            return currentTime - lastResetTime.get() > 300000; // 5 minutes
        }
    }
}