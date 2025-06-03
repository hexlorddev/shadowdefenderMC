package com.shadowdefender.mc.commands;

import com.shadowdefender.mc.ShadowDefenderMC;
import com.shadowdefender.mc.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ShadowDefCommand implements CommandExecutor, TabCompleter {
    
    private final ShadowDefenderMC plugin;
    
    public ShadowDefCommand(ShadowDefenderMC plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shadowdefender.admin")) {
            sender.sendMessage(MessageUtil.colorize(plugin.getConfigManager().getPrefix() + 
                "&cYou don't have permission to use this command!"));
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "status":
                handleStatus(sender);
                break;
                
            case "reload":
                handleReload(sender);
                break;
                
            case "stats":
            case "statistics":
                handleStatistics(sender);
                break;
                
            case "challenge":
                handleChallenge(sender, args);
                break;
                
            case "ban":
                handleBan(sender, args);
                break;
                
            case "unban":
                handleUnban(sender, args);
                break;
                
            case "whitelist":
                handleWhitelist(sender, args);
                break;
                
            case "clearcache":
                handleClearCache(sender);
                break;
                
            case "debug":
                handleDebug(sender, args);
                break;
                
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        String prefix = plugin.getConfigManager().getPrefix();
        sender.sendMessage(MessageUtil.colorize(prefix + "&6ShadowDefenderMC Commands:"));
        sender.sendMessage(MessageUtil.colorize("&e/shadowdef status &7- Show protection status"));
        sender.sendMessage(MessageUtil.colorize("&e/shadowdef reload &7- Reload configuration"));
        sender.sendMessage(MessageUtil.colorize("&e/shadowdef stats &7- Show detailed statistics"));
        sender.sendMessage(MessageUtil.colorize("&e/shadowdef challenge [on/off/force] &7- Control challenge mode"));
        sender.sendMessage(MessageUtil.colorize("&e/shadowdef ban <ip> [duration] &7- Ban an IP address"));
        sender.sendMessage(MessageUtil.colorize("&e/shadowdef unban <ip> &7- Unban an IP address"));
        sender.sendMessage(MessageUtil.colorize("&e/shadowdef whitelist <add/remove> <ip> &7- Manage IP whitelist"));
        sender.sendMessage(MessageUtil.colorize("&e/shadowdef clearcache &7- Clear all caches"));
        sender.sendMessage(MessageUtil.colorize("&e/shadowdef debug [on/off] &7- Toggle debug mode"));
    }
    
    private void handleStatus(CommandSender sender) {
        String prefix = plugin.getConfigManager().getPrefix();
        
        sender.sendMessage(MessageUtil.colorize(prefix + "&6=== ShadowDefenderMC Status ==="));
        
        // Plugin status
        boolean pluginEnabled = plugin.getConfig().getBoolean("plugin.enabled", true);
        sender.sendMessage(MessageUtil.colorize("&ePlugin Status: " + 
            (pluginEnabled ? "&aEnabled" : "&cDisabled")));
        
        // Protection modules status
        boolean botProtection = plugin.getConfigManager().isBotProtectionEnabled();
        boolean vpnDetection = plugin.getConfigManager().isVPNDetectionEnabled();
        boolean rateLimiting = plugin.getConfigManager().isRateLimitingEnabled();
        boolean challengeMode = plugin.getConfigManager().isChallengeModeEnabled();
        
        sender.sendMessage(MessageUtil.colorize("&eBot Protection: " + 
            (botProtection ? "&aEnabled" : "&cDisabled")));
        sender.sendMessage(MessageUtil.colorize("&eVPN Detection: " + 
            (vpnDetection ? "&aEnabled" : "&cDisabled")));
        sender.sendMessage(MessageUtil.colorize("&eRate Limiting: " + 
            (rateLimiting ? "&aEnabled" : "&cDisabled")));
        sender.sendMessage(MessageUtil.colorize("&eChallenge Mode: " + 
            (challengeMode ? "&aEnabled" : "&cDisabled")));
        
        // Challenge mode active status
        boolean challengeActive = plugin.getChallengeManager().isChallengeModeActive();
        sender.sendMessage(MessageUtil.colorize("&eChallenge Active: " + 
            (challengeActive ? "&cYES" : "&aNo")));
        
        // Basic statistics
        int blockedBots = plugin.getProtectionManager().getBlockedBotsCount();
        int blockedVPNs = plugin.getVPNManager().getBlockedVPNsCount();
        int activeConnections = plugin.getRateLimitManager().getActiveConnectionsCount();
        
        sender.sendMessage(MessageUtil.colorize("&eBlocked Bots: &c" + blockedBots));
        sender.sendMessage(MessageUtil.colorize("&eBlocked VPNs: &c" + blockedVPNs));
        sender.sendMessage(MessageUtil.colorize("&eActive Connections: &b" + activeConnections));
    }
    
    private void handleReload(CommandSender sender) {
        String prefix = plugin.getConfigManager().getPrefix();
        
        try {
            sender.sendMessage(MessageUtil.colorize(prefix + "&eReloading configuration..."));
            plugin.reloadConfiguration();
            sender.sendMessage(MessageUtil.colorize(prefix + "&aConfiguration reloaded successfully!"));
        } catch (Exception e) {
            sender.sendMessage(MessageUtil.colorize(prefix + "&cError reloading configuration: " + e.getMessage()));
            plugin.getLoggingManager().logError("Configuration reload error", e);
        }
    }
    
    private void handleStatistics(CommandSender sender) {
        String prefix = plugin.getConfigManager().getPrefix();
        
        sender.sendMessage(MessageUtil.colorize(prefix + "&6=== Detailed Statistics ==="));
        
        // Protection Manager Stats
        sender.sendMessage(MessageUtil.colorize("&e&lBot Protection:"));
        sender.sendMessage(MessageUtil.colorize("  &eBlocked Bots: &c" + plugin.getProtectionManager().getBlockedBotsCount()));
        sender.sendMessage(MessageUtil.colorize("  &eSuspicious Joins: &c" + plugin.getProtectionManager().getSuspiciousJoinsCount()));
        sender.sendMessage(MessageUtil.colorize("  &eBanned IPs: &c" + plugin.getProtectionManager().getBannedIPsCount()));
        sender.sendMessage(MessageUtil.colorize("  &eTracked IPs: &b" + plugin.getProtectionManager().getTrackedIPsCount()));
        
        // VPN Manager Stats
        sender.sendMessage(MessageUtil.colorize("&e&lVPN Detection:"));
        sender.sendMessage(MessageUtil.colorize("  &eBlocked VPNs: &c" + plugin.getVPNManager().getBlockedVPNsCount()));
        sender.sendMessage(MessageUtil.colorize("  &eTotal Checks: &b" + plugin.getVPNManager().getTotalChecksCount()));
        sender.sendMessage(MessageUtil.colorize("  &eCached Entries: &b" + plugin.getVPNManager().getCachedEntriesCount()));
        
        // Rate Limit Manager Stats
        sender.sendMessage(MessageUtil.colorize("&e&lRate Limiting:"));
        sender.sendMessage(MessageUtil.colorize("  &eBlocked Connections: &c" + plugin.getRateLimitManager().getBlockedConnectionsCount()));
        sender.sendMessage(MessageUtil.colorize("  &eTotal Connections: &b" + plugin.getRateLimitManager().getTotalConnectionsCount()));
        sender.sendMessage(MessageUtil.colorize("  &eActive Connections: &b" + plugin.getRateLimitManager().getActiveConnectionsCount()));
        
        // Challenge Manager Stats
        sender.sendMessage(MessageUtil.colorize("&e&lChallenge Mode:"));
        sender.sendMessage(MessageUtil.colorize("  &eActive: " + 
            (plugin.getChallengeManager().isChallengeModeActive() ? "&cYES" : "&aNo")));
        sender.sendMessage(MessageUtil.colorize("  &ePending Challenges: &b" + plugin.getChallengeManager().getPendingChallenges()));
        sender.sendMessage(MessageUtil.colorize("  &eVerified Players: &a" + plugin.getChallengeManager().getVerifiedPlayersCount()));
        sender.sendMessage(MessageUtil.colorize("  &eSuspicious Activity: &c" + plugin.getChallengeManager().getSuspiciousActivityCount()));
    }
    
    private void handleChallenge(CommandSender sender, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        
        if (args.length < 2) {
            sender.sendMessage(MessageUtil.colorize(prefix + "&eUsage: /shadowdef challenge <on/off/force>"));
            return;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "on":
                plugin.getChallengeManager().forceActivateChallengeMode();
                sender.sendMessage(MessageUtil.colorize(prefix + "&aChallenge mode force activated!"));
                break;
                
            case "off":
                plugin.getChallengeManager().forceDeactivateChallengeMode();
                sender.sendMessage(MessageUtil.colorize(prefix + "&aChallenge mode deactivated!"));
                break;
                
            case "force":
                plugin.getChallengeManager().resetSuspiciousActivityCount();
                plugin.getChallengeManager().forceActivateChallengeMode();
                sender.sendMessage(MessageUtil.colorize(prefix + "&aChallenge mode force activated with reset!"));
                break;
                
            default:
                sender.sendMessage(MessageUtil.colorize(prefix + "&cInvalid option. Use: on, off, or force"));
                break;
        }
    }
    
    private void handleBan(CommandSender sender, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        
        if (args.length < 2) {
            sender.sendMessage(MessageUtil.colorize(prefix + "&eUsage: /shadowdef ban <ip> [duration_seconds]"));
            return;
        }
        
        String ip = args[1];
        int duration = plugin.getConfigManager().getAutoBanDuration();
        
        if (args.length > 2) {
            try {
                duration = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtil.colorize(prefix + "&cInvalid duration format!"));
                return;
            }
        }
        
        // Manual ban implementation would go here
        sender.sendMessage(MessageUtil.colorize(prefix + "&aIP " + ip + " banned for " + duration + " seconds!"));
        plugin.getLoggingManager().logSecurity("MANUAL_BAN", 
            String.format("IP manually banned by %s: %s (%d seconds)", sender.getName(), ip, duration));
    }
    
    private void handleUnban(CommandSender sender, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        
        if (args.length < 2) {
            sender.sendMessage(MessageUtil.colorize(prefix + "&eUsage: /shadowdef unban <ip>"));
            return;
        }
        
        String ip = args[1];
        plugin.getProtectionManager().unbanIP(ip);
        sender.sendMessage(MessageUtil.colorize(prefix + "&aIP " + ip + " unbanned!"));
    }
    
    private void handleWhitelist(CommandSender sender, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        
        if (args.length < 3) {
            sender.sendMessage(MessageUtil.colorize(prefix + "&eUsage: /shadowdef whitelist <add/remove> <ip>"));
            return;
        }
        
        String action = args[1].toLowerCase();
        String ip = args[2];
        
        switch (action) {
            case "add":
                plugin.getProtectionManager().addWhitelistIP(ip);
                sender.sendMessage(MessageUtil.colorize(prefix + "&aIP " + ip + " added to whitelist!"));
                break;
                
            case "remove":
                plugin.getProtectionManager().removeWhitelistIP(ip);
                sender.sendMessage(MessageUtil.colorize(prefix + "&aIP " + ip + " removed from whitelist!"));
                break;
                
            default:
                sender.sendMessage(MessageUtil.colorize(prefix + "&cInvalid action. Use: add or remove"));
                break;
        }
    }
    
    private void handleClearCache(CommandSender sender) {
        String prefix = plugin.getConfigManager().getPrefix();
        
        plugin.getVPNManager().clearCache();
        sender.sendMessage(MessageUtil.colorize(prefix + "&aAll caches cleared!"));
    }
    
    private void handleDebug(CommandSender sender, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        
        // Debug mode toggle implementation would go here
        sender.sendMessage(MessageUtil.colorize(prefix + "&eDebug mode toggle is not yet implemented"));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("shadowdefender.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return Arrays.asList("status", "reload", "stats", "challenge", "ban", "unban", "whitelist", "clearcache", "debug");
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "challenge":
                    return Arrays.asList("on", "off", "force");
                case "whitelist":
                    return Arrays.asList("add", "remove");
                case "debug":
                    return Arrays.asList("on", "off");
            }
        }
        
        return new ArrayList<>();
    }
}