package com.shadowdefender.mc.managers;

import com.shadowdefender.mc.ShadowDefenderMC;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ConfigManager {
    
    private final ShadowDefenderMC plugin;
    private List<Pattern> suspiciousUsernamePatterns;
    
    public ConfigManager(ShadowDefenderMC plugin) {
        this.plugin = plugin;
    }
    
    public boolean loadConfiguration() {
        try {
            loadSuspiciousUsernamePatterns();
            validateConfiguration();
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Error loading configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void loadSuspiciousUsernamePatterns() {
        List<String> patterns = plugin.getConfig().getStringList("bot_protection.suspicious_username_patterns");
        suspiciousUsernamePatterns = patterns.stream()
                .map(pattern -> {
                    try {
                        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                    } catch (PatternSyntaxException e) {
                        plugin.getLogger().warning("Invalid regex pattern: " + pattern + " - " + e.getMessage());
                        return null;
                    }
                })
                .filter(pattern -> pattern != null)
                .collect(Collectors.toList());
                
        plugin.getLogger().info("Loaded " + suspiciousUsernamePatterns.size() + " suspicious username patterns");
    }
    
    private void validateConfiguration() {
        // Validate bot protection settings
        int maxJoinsPerSecond = plugin.getConfig().getInt("bot_protection.max_joins_per_ip_per_second", 3);
        if (maxJoinsPerSecond < 1 || maxJoinsPerSecond > 20) {
            plugin.getLogger().warning("max_joins_per_ip_per_second should be between 1-20, using default: 3");
            plugin.getConfig().set("bot_protection.max_joins_per_ip_per_second", 3);
        }
        
        int maxJoinsPer10Seconds = plugin.getConfig().getInt("bot_protection.max_joins_per_ip_per_10_seconds", 5);
        if (maxJoinsPer10Seconds < maxJoinsPerSecond) {
            plugin.getLogger().warning("max_joins_per_ip_per_10_seconds should be >= max_joins_per_ip_per_second");
            plugin.getConfig().set("bot_protection.max_joins_per_ip_per_10_seconds", maxJoinsPerSecond + 2);
        }
        
        // Validate VPN detection settings
        String apiProvider = plugin.getConfig().getString("vpn_detection.api_provider", "ip-api");
        if (!isValidApiProvider(apiProvider)) {
            plugin.getLogger().warning("Invalid API provider: " + apiProvider + ", using default: ip-api");
            plugin.getConfig().set("vpn_detection.api_provider", "ip-api");
        }
        
        // Validate challenge mode settings
        int triggerThreshold = plugin.getConfig().getInt("challenge_mode.trigger_threshold", 10);
        if (triggerThreshold < 5) {
            plugin.getLogger().warning("challenge_mode.trigger_threshold should be at least 5, using default: 10");
            plugin.getConfig().set("challenge_mode.trigger_threshold", 10);
        }
    }
    
    private boolean isValidApiProvider(String provider) {
        return provider.equals("ip-api") || provider.equals("iphub") || provider.equals("proxycheck");
    }
    
    // Configuration getters with default values
    public boolean isBotProtectionEnabled() {
        return plugin.getConfig().getBoolean("bot_protection.enabled", true);
    }
    
    public int getMaxJoinsPerSecond() {
        return plugin.getConfig().getInt("bot_protection.max_joins_per_ip_per_second", 3);
    }
    
    public int getMaxJoinsPer10Seconds() {
        return plugin.getConfig().getInt("bot_protection.max_joins_per_ip_per_10_seconds", 5);
    }
    
    public int getAutoBanDuration() {
        return plugin.getConfig().getInt("bot_protection.auto_ban_duration", 300);
    }
    
    public List<String> getWhitelistUsernames() {
        return plugin.getConfig().getStringList("bot_protection.whitelist_usernames");
    }
    
    public List<Pattern> getSuspiciousUsernamePatterns() {
        return suspiciousUsernamePatterns;
    }
    
    public boolean isVPNDetectionEnabled() {
        return plugin.getConfig().getBoolean("vpn_detection.enabled", true);
    }
    
    public String getVPNApiProvider() {
        return plugin.getConfig().getString("vpn_detection.api_provider", "ip-api");
    }
    
    public int getVPNApiTimeout() {
        return plugin.getConfig().getInt("vpn_detection.timeout", 5000);
    }
    
    public String getVPNAction() {
        return plugin.getConfig().getString("vpn_detection.action", "kick");
    }
    
    public String getVPNKickMessage() {
        return plugin.getConfig().getString("vpn_detection.message", "&cVPN/Proxy connections are not allowed on this server!");
    }
    
    public List<String> getVPNWhitelistIPs() {
        return plugin.getConfig().getStringList("vpn_detection.whitelist_ips");
    }
    
    public boolean isChallengeModeEnabled() {
        return plugin.getConfig().getBoolean("challenge_mode.enabled", true);
    }
    
    public int getChallengeTriggerThreshold() {
        return plugin.getConfig().getInt("challenge_mode.trigger_threshold", 10);
    }
    
    public int getChallengeDuration() {
        return plugin.getConfig().getInt("challenge_mode.duration", 300);
    }
    
    public int getVerificationTimeout() {
        return plugin.getConfig().getInt("challenge_mode.verification_timeout", 60);
    }
    
    public String getLimboWorld() {
        return plugin.getConfig().getString("challenge_mode.limbo_world", "limbo");
    }
    
    public String getChallengeWelcomeMessage() {
        return plugin.getConfig().getString("challenge_mode.welcome_message", 
            "&e&lSERVER PROTECTION ACTIVE\n&7Please verify yourself by typing:\n&a/verify {CODE}\n&7Your verification code: &b{CODE}");
    }
    
    public String getChallengeKickMessage() {
        return plugin.getConfig().getString("challenge_mode.kick_message", 
            "&cVerification timeout! Please rejoin and verify quickly.");
    }
    
    public boolean isRateLimitingEnabled() {
        return plugin.getConfig().getBoolean("rate_limiting.enabled", true);
    }
    
    public int getMaxPacketsPerSecond() {
        return plugin.getConfig().getInt("rate_limiting.max_packets_per_second", 100);
    }
    
    public int getMaxConnectionsPerIP() {
        return plugin.getConfig().getInt("rate_limiting.max_connections_per_ip", 5);
    }
    
    public int getConnectionThrottle() {
        return plugin.getConfig().getInt("rate_limiting.connection_throttle", 1000);
    }
    
    public boolean isConsoleLoggingEnabled() {
        return plugin.getConfig().getBoolean("logging.console", true);
    }
    
    public boolean isFileLoggingEnabled() {
        return plugin.getConfig().getBoolean("logging.file", true);
    }
    
    public String getLogFilePath() {
        return plugin.getConfig().getString("logging.file_path", "plugins/ShadowDefenderMC/logs/security.log");
    }
    
    public long getMaxLogFileSize() {
        return plugin.getConfig().getLong("logging.max_file_size", 10485760);
    }
    
    public String getLogLevel() {
        return plugin.getConfig().getString("logging.log_level", "INFO");
    }
    
    public String getMessage(String key) {
        return plugin.getConfig().getString("messages." + key, "&cMessage not found: " + key);
    }
    
    public String getPrefix() {
        return plugin.getConfig().getString("messages.prefix", "&8[&6ShadowDefender&8] ");
    }
    
    public boolean isAsyncProcessingEnabled() {
        return plugin.getConfig().getBoolean("advanced.use_async_processing", true);
    }
    
    public int getCacheDuration() {
        return plugin.getConfig().getInt("advanced.cache_duration", 300);
    }
    
    public int getCleanupInterval() {
        return plugin.getConfig().getInt("advanced.cleanup_interval", 60);
    }
    
    public int getMaxCachedIPs() {
        return plugin.getConfig().getInt("advanced.max_cached_ips", 10000);
    }
    
    public int getThreadPoolSize() {
        return plugin.getConfig().getInt("advanced.thread_pool_size", 4);
    }
    
    // API Configuration getters
    public ConfigurationSection getApiConfig(String provider) {
        return plugin.getConfig().getConfigurationSection("vpn_detection.apis." + provider);
    }
    
    public String getApiUrl(String provider) {
        ConfigurationSection apiConfig = getApiConfig(provider);
        return apiConfig != null ? apiConfig.getString("url", "") : "";
    }
    
    public String getApiKey(String provider) {
        ConfigurationSection apiConfig = getApiConfig(provider);
        return apiConfig != null ? apiConfig.getString("api_key", "") : "";
    }
    
    public boolean isApiEnabled(String provider) {
        ConfigurationSection apiConfig = getApiConfig(provider);
        return apiConfig != null && apiConfig.getBoolean("enabled", false);
    }
}