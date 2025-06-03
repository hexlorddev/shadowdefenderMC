package com.shadowdefender.mc;

import com.shadowdefender.mc.commands.ShadowDefCommand;
import com.shadowdefender.mc.commands.VerifyCommand;
import com.shadowdefender.mc.listeners.PlayerConnectionListener;
import com.shadowdefender.mc.managers.*;
import com.shadowdefender.mc.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ShadowDefenderMC extends JavaPlugin {
    
    private static ShadowDefenderMC instance;
    
    // Managers
    private ConfigManager configManager;
    private ProtectionManager protectionManager;
    private VPNDetectionManager vpnManager;
    private ChallengeManager challengeManager;
    private LoggingManager loggingManager;
    private RateLimitManager rateLimitManager;
    
    // Thread pools
    private ExecutorService asyncExecutor;
    private ScheduledExecutorService scheduledExecutor;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // ASCII Art Banner
        getLogger().info("  _____ _               _               _____        __               _            ");
        getLogger().info(" / ____| |             | |             |  __ \\      / _|             | |           ");
        getLogger().info("| (___ | |__   __ _  __| | _____      _| |  | | ___| |_ ___ _ __   __| | ___ _ __ ");
        getLogger().info(" \\___ \\| '_ \\ / _` |/ _` |/ _ \\ \\ /\\ / / |  | |/ _ \\  _/ _ \\ '_ \\ / _` |/ _ \\ '__|");
        getLogger().info(" ____) | | | | (_| | (_| | (_) \\ V  V /| |__| |  __/ ||  __/ | | | (_| |  __/ |   ");
        getLogger().info("|_____/|_| |_|\\__,_|\\__,_|\\___/ \\_/\\_/ |_____/ \\___|_| \\___|_| |_|\\__,_|\\___|_|   ");
        getLogger().info("                                                                                 ");
        getLogger().info("ShadowDefenderMC v1.0.0 - High-Performance Anti-DDoS Protection");
        getLogger().info("Compatible with Paper, Spigot, and BungeeCord");
        
        // Initialize configuration
        if (!initializeConfiguration()) {
            getLogger().severe("Failed to initialize configuration! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize thread pools
        initializeThreadPools();
        
        // Initialize managers
        if (!initializeManagers()) {
            getLogger().severe("Failed to initialize managers! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Register listeners and commands
        registerListenersAndCommands();
        
        // Start scheduled tasks
        startScheduledTasks();
        
        getLogger().info("ShadowDefenderMC has been enabled successfully!");
        getLogger().info("Protection systems active: Bot Detection, VPN Filtering, Rate Limiting");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("ShadowDefenderMC is shutting down...");
        
        // Shutdown thread pools
        if (asyncExecutor != null && !asyncExecutor.isShutdown()) {
            asyncExecutor.shutdown();
            try {
                if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    asyncExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                asyncExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
            scheduledExecutor.shutdown();
            try {
                if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduledExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        // Close managers
        if (loggingManager != null) {
            loggingManager.close();
        }
        
        getLogger().info("ShadowDefenderMC has been disabled successfully!");
    }
    
    private boolean initializeConfiguration() {
        try {
            // Create plugin directory if it doesn't exist
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            
            // Create logs directory
            File logsDir = new File(getDataFolder(), "logs");
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }
            
            // Save default config if it doesn't exist
            saveDefaultConfig();
            
            // Initialize config manager
            configManager = new ConfigManager(this);
            return configManager.loadConfiguration();
            
        } catch (Exception e) {
            getLogger().severe("Error initializing configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void initializeThreadPools() {
        int threadPoolSize = getConfig().getInt("advanced.thread_pool_size", 4);
        asyncExecutor = Executors.newFixedThreadPool(threadPoolSize);
        scheduledExecutor = Executors.newScheduledThreadPool(2);
    }
    
    private boolean initializeManagers() {
        try {
            // Initialize logging first
            loggingManager = new LoggingManager(this);
            
            // Initialize other managers
            protectionManager = new ProtectionManager(this);
            vpnManager = new VPNDetectionManager(this);
            challengeManager = new ChallengeManager(this);
            rateLimitManager = new RateLimitManager(this);
            
            return true;
            
        } catch (Exception e) {
            getLogger().severe("Error initializing managers: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void registerListenersAndCommands() {
        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        
        // Register commands
        getCommand("shadowdef").setExecutor(new ShadowDefCommand(this));
        getCommand("verify").setExecutor(new VerifyCommand(this));
    }
    
    private void startScheduledTasks() {
        // Cleanup task
        int cleanupInterval = getConfig().getInt("advanced.cleanup_interval", 60);
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                protectionManager.cleanupExpiredEntries();
                rateLimitManager.cleanupExpiredEntries();
                challengeManager.cleanupExpiredChallenges();
            } catch (Exception e) {
                getLogger().warning("Error during cleanup task: " + e.getMessage());
            }
        }, cleanupInterval, cleanupInterval, TimeUnit.SECONDS);
        
        // Statistics logging task
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                logStatistics();
            } catch (Exception e) {
                getLogger().warning("Error during statistics logging: " + e.getMessage());
            }
        }, 300, 300, TimeUnit.SECONDS); // Every 5 minutes
    }
    
    private void logStatistics() {
        if (!getConfig().getBoolean("logging.console", true)) return;
        
        int blockedBots = protectionManager.getBlockedBotsCount();
        int blockedVPNs = vpnManager.getBlockedVPNsCount();
        int activeConnections = rateLimitManager.getActiveConnectionsCount();
        
        getLogger().info(String.format("Statistics - Blocked Bots: %d, Blocked VPNs: %d, Active Connections: %d", 
                                     blockedBots, blockedVPNs, activeConnections));
    }
    
    public void reloadConfiguration() {
        try {
            reloadConfig();
            configManager.loadConfiguration();
            
            // Reload managers
            protectionManager.reloadConfiguration();
            vpnManager.reloadConfiguration();
            challengeManager.reloadConfiguration();
            rateLimitManager.reloadConfiguration();
            
            getLogger().info("Configuration reloaded successfully!");
            
        } catch (Exception e) {
            getLogger().severe("Error reloading configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Getters for managers and utilities
    public static ShadowDefenderMC getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public ProtectionManager getProtectionManager() {
        return protectionManager;
    }
    
    public VPNDetectionManager getVPNManager() {
        return vpnManager;
    }
    
    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }
    
    public LoggingManager getLoggingManager() {
        return loggingManager;
    }
    
    public RateLimitManager getRateLimitManager() {
        return rateLimitManager;
    }
    
    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }
    
    public ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }
}