package com.shadowdefender.mc.bungee;

import com.shadowdefender.mc.bungee.listeners.BungeeConnectionListener;
import com.shadowdefender.mc.bungee.managers.BungeeConfigManager;
import com.shadowdefender.mc.bungee.managers.BungeeProtectionManager;
import com.shadowdefender.mc.bungee.managers.BungeeVPNManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ShadowDefenderBungee extends Plugin {
    
    private static ShadowDefenderBungee instance;
    
    // Managers
    private BungeeConfigManager configManager;
    private BungeeProtectionManager protectionManager;
    private BungeeVPNManager vpnManager;
    
    // Thread pools
    private ExecutorService asyncExecutor;
    private ScheduledExecutorService scheduledExecutor;
    
    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("  _____ _               _               _____        __               _            ");
        getLogger().info(" / ____| |             | |             |  __ \\      / _|             | |           ");
        getLogger().info("| (___ | |__   __ _  __| | _____      _| |  | | ___| |_ ___ _ __   __| | ___ _ __ ");
        getLogger().info(" \\___ \\| '_ \\ / _` |/ _` |/ _ \\ \\ /\\ / / |  | |/ _ \\  _/ _ \\ '_ \\ / _` |/ _ \\ '__|");
        getLogger().info(" ____) | | | | (_| | (_| | (_) \\ V  V /| |__| |  __/ ||  __/ | | | (_| |  __/ |   ");
        getLogger().info("|_____/|_| |_|\\__,_|\\__,_|\\___/ \\_/\\_/ |_____/ \\___|_| \\___|_| |_|\\__,_|\\___|_|   ");
        getLogger().info("                                                                                 ");
        getLogger().info("ShadowDefenderMC v1.0.0 - BungeeCord Anti-DDoS Protection");
        
        // Initialize configuration
        if (!initializeConfiguration()) {
            getLogger().severe("Failed to initialize configuration! Disabling plugin...");
            return;
        }
        
        // Initialize thread pools
        initializeThreadPools();
        
        // Initialize managers
        if (!initializeManagers()) {
            getLogger().severe("Failed to initialize managers! Disabling plugin...");
            return;
        }
        
        // Register listeners
        registerListeners();
        
        // Start scheduled tasks
        startScheduledTasks();
        
        getLogger().info("ShadowDefenderMC BungeeCord has been enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("ShadowDefenderMC BungeeCord is shutting down...");
        
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
        
        getLogger().info("ShadowDefenderMC BungeeCord has been disabled successfully!");
    }
    
    private boolean initializeConfiguration() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            
            configManager = new BungeeConfigManager(this);
            return configManager.loadConfiguration();
            
        } catch (Exception e) {
            getLogger().severe("Error initializing configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void initializeThreadPools() {
        asyncExecutor = Executors.newFixedThreadPool(4);
        scheduledExecutor = Executors.newScheduledThreadPool(2);
    }
    
    private boolean initializeManagers() {
        try {
            protectionManager = new BungeeProtectionManager(this);
            vpnManager = new BungeeVPNManager(this);
            
            return true;
            
        } catch (Exception e) {
            getLogger().severe("Error initializing managers: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new BungeeConnectionListener(this));
    }
    
    private void startScheduledTasks() {
        // Cleanup task
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                protectionManager.cleanupExpiredEntries();
            } catch (Exception e) {
                getLogger().warning("Error during cleanup task: " + e.getMessage());
            }
        }, 60, 60, TimeUnit.SECONDS);
        
        // Statistics logging task
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                logStatistics();
            } catch (Exception e) {
                getLogger().warning("Error during statistics logging: " + e.getMessage());
            }
        }, 300, 300, TimeUnit.SECONDS);
    }
    
    private void logStatistics() {
        int blockedBots = protectionManager.getBlockedBotsCount();
        int blockedVPNs = vpnManager.getBlockedVPNsCount();
        
        getLogger().info(String.format("BungeeCord Statistics - Blocked Bots: %d, Blocked VPNs: %d", 
                                     blockedBots, blockedVPNs));
    }
    
    public void reloadConfiguration() {
        try {
            configManager.loadConfiguration();
            protectionManager.reloadConfiguration();
            vpnManager.reloadConfiguration();
            
            getLogger().info("BungeeCord configuration reloaded successfully!");
            
        } catch (Exception e) {
            getLogger().severe("Error reloading configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Getters
    public static ShadowDefenderBungee getInstance() {
        return instance;
    }
    
    public BungeeConfigManager getConfigManager() {
        return configManager;
    }
    
    public BungeeProtectionManager getProtectionManager() {
        return protectionManager;
    }
    
    public BungeeVPNManager getVPNManager() {
        return vpnManager;
    }
    
    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }
    
    public ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }
}