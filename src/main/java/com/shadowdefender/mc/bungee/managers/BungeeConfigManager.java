package com.shadowdefender.mc.bungee.managers;

import com.shadowdefender.mc.bungee.ShadowDefenderBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.List;

public class BungeeConfigManager {
    
    private final ShadowDefenderBungee plugin;
    private Configuration config;
    
    public BungeeConfigManager(ShadowDefenderBungee plugin) {
        this.plugin = plugin;
    }
    
    public boolean loadConfiguration() {
        try {
            // Create config file if it doesn't exist
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                createDefaultConfig(configFile);
            }
            
            // Load configuration
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            
            plugin.getLogger().info("BungeeCord configuration loaded successfully");
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error loading BungeeCord configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void createDefaultConfig(File configFile) throws IOException {
        // Create a simplified config for BungeeCord
        StringBuilder configContent = new StringBuilder();
        configContent.append("# ShadowDefenderMC BungeeCord Configuration\\n\\n");
        configContent.append("# Bot Protection\\n");
        configContent.append("bot_protection:\\n");
        configContent.append("  enabled: true\\n");
        configContent.append("  max_joins_per_ip_per_second: 2\\n");
        configContent.append("  max_joins_per_ip_per_10_seconds: 4\\n");
        configContent.append("  auto_ban_duration: 300\\n\\n");
        
        configContent.append("# VPN Detection\\n");
        configContent.append("vpn_detection:\\n");
        configContent.append("  enabled: true\\n");
        configContent.append("  api_provider: 'ip-api'\\n");
        configContent.append("  timeout: 5000\\n");
        configContent.append("  action: 'kick'\\n\\n");
        
        configContent.append("# Messages\\n");
        configContent.append("messages:\\n");
        configContent.append("  bot_kick: '&cConnection blocked: Suspicious activity detected'\\n");
        configContent.append("  vpn_kick: '&cVPN/Proxy connections are not allowed'\\n");
        
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(configContent.toString());
        }
    }
    
    // Configuration getters
    public boolean isBotProtectionEnabled() {
        return config.getBoolean("bot_protection.enabled", true);
    }
    
    public int getMaxJoinsPerSecond() {
        return config.getInt("bot_protection.max_joins_per_ip_per_second", 2);
    }
    
    public int getMaxJoinsPer10Seconds() {
        return config.getInt("bot_protection.max_joins_per_ip_per_10_seconds", 4);
    }
    
    public int getAutoBanDuration() {
        return config.getInt("bot_protection.auto_ban_duration", 300);
    }
    
    public boolean isVPNDetectionEnabled() {
        return config.getBoolean("vpn_detection.enabled", true);
    }
    
    public String getVPNApiProvider() {
        return config.getString("vpn_detection.api_provider", "ip-api");
    }
    
    public int getVPNApiTimeout() {
        return config.getInt("vpn_detection.timeout", 5000);
    }
    
    public String getVPNAction() {
        return config.getString("vpn_detection.action", "kick");
    }
    
    public String getBotKickMessage() {
        return config.getString("messages.bot_kick", "&cConnection blocked: Suspicious activity detected");
    }
    
    public String getVPNKickMessage() {
        return config.getString("messages.vpn_kick", "&cVPN/Proxy connections are not allowed");
    }
}