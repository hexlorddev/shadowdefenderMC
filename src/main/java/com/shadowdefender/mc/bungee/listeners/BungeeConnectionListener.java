package com.shadowdefender.mc.bungee.listeners;

import com.shadowdefender.mc.bungee.ShadowDefenderBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeConnectionListener implements Listener {
    
    private final ShadowDefenderBungee plugin;
    
    public BungeeConnectionListener(ShadowDefenderBungee plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(PreLoginEvent event) {
        String ipAddress = event.getConnection().getAddress().getAddress().getHostAddress();
        String playerName = event.getConnection().getName();
        
        try {
            // Check bot protection
            if (!plugin.getProtectionManager().isConnectionAllowed(ipAddress, playerName)) {
                String message = ChatColor.translateAlternateColorCodes('&', 
                    plugin.getConfigManager().getBotKickMessage());
                event.setCancelReason(message);
                event.setCancelled(true);
                return;
            }
            
            // Check VPN detection
            if (!plugin.getVPNManager().isVPNAllowed(ipAddress)) {
                String message = ChatColor.translateAlternateColorCodes('&', 
                    plugin.getConfigManager().getVPNKickMessage());
                event.setCancelReason(message);
                event.setCancelled(true);
                return;
            }
            
            // All checks passed
            plugin.getLogger().fine("Connection allowed: " + playerName + " (" + ipAddress + ")");
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error during pre-login checks for " + playerName + ": " + e.getMessage());
            // Allow connection on error to avoid false positives
        }
    }
}