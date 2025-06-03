package com.shadowdefender.mc.managers;

import com.shadowdefender.mc.ShadowDefenderMC;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class LoggingManager {
    
    private final ShadowDefenderMC plugin;
    private final ExecutorService loggingExecutor;
    private final ConcurrentLinkedQueue<LogEntry> logQueue;
    
    private PrintWriter fileWriter;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public LoggingManager(ShadowDefenderMC plugin) {
        this.plugin = plugin;
        this.loggingExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "ShadowDefender-Logger");
            t.setDaemon(true);
            return t;
        });
        this.logQueue = new ConcurrentLinkedQueue<>();
        
        initializeFileLogging();
        startLoggingTask();
    }
    
    private void initializeFileLogging() {
        if (!plugin.getConfigManager().isFileLoggingEnabled()) {
            return;
        }
        
        try {
            String logFilePath = plugin.getConfigManager().getLogFilePath();
            Path logPath = Paths.get(logFilePath);
            
            // Create directories if they don't exist
            Files.createDirectories(logPath.getParent());
            
            // Check file size and rotate if necessary
            rotateLogFileIfNeeded(logPath);
            
            // Create file writer
            fileWriter = new PrintWriter(new FileWriter(logPath.toFile(), true));
            
            plugin.getLogger().info("File logging initialized: " + logPath.toAbsolutePath());
            
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to initialize file logging: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void rotateLogFileIfNeeded(Path logPath) throws IOException {
        File logFile = logPath.toFile();
        if (!logFile.exists()) {
            return;
        }
        
        long maxSize = plugin.getConfigManager().getMaxLogFileSize();
        if (logFile.length() > maxSize) {
            // Rotate log file
            String backupName = logPath.getFileName().toString() + ".old";
            Path backupPath = logPath.getParent().resolve(backupName);
            
            // Delete old backup if exists
            Files.deleteIfExists(backupPath);
            
            // Move current log to backup
            Files.move(logPath, backupPath);
            
            plugin.getLogger().info("Log file rotated: " + backupPath.toAbsolutePath());
        }
    }
    
    private void startLoggingTask() {
        loggingExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    processLogQueue();
                    Thread.sleep(100); // Process logs every 100ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    plugin.getLogger().warning("Error in logging task: " + e.getMessage());
                }
            }
        });
    }
    
    private void processLogQueue() {
        LogEntry entry;
        while ((entry = logQueue.poll()) != null) {
            writeLogEntry(entry);
        }
        
        // Flush file writer if exists
        if (fileWriter != null) {
            fileWriter.flush();
        }
    }
    
    private void writeLogEntry(LogEntry entry) {
        String formattedMessage = formatLogMessage(entry);
        
        // Console logging
        if (plugin.getConfigManager().isConsoleLoggingEnabled()) {
            Level level = getLogLevel(entry.getLevel());
            plugin.getLogger().log(level, formattedMessage);
        }
        
        // File logging
        if (fileWriter != null && plugin.getConfigManager().isFileLoggingEnabled()) {
            fileWriter.println(formattedMessage);
        }
    }
    
    private String formatLogMessage(LogEntry entry) {
        return String.format("[%s] [%s] %s", 
            entry.getTimestamp().format(dateFormatter),
            entry.getLevel(),
            entry.getMessage());
    }
    
    private Level getLogLevel(String levelString) {
        switch (levelString.toUpperCase()) {
            case "DEBUG":
                return Level.FINE;
            case "INFO":
                return Level.INFO;
            case "WARN":
                return Level.WARNING;
            case "ERROR":
                return Level.SEVERE;
            default:
                return Level.INFO;
        }
    }
    
    private boolean shouldLog(String level) {
        String configLevel = plugin.getConfigManager().getLogLevel().toUpperCase();
        
        switch (configLevel) {
            case "DEBUG":
                return true; // Log everything
            case "INFO":
                return !level.equals("DEBUG");
            case "WARN":
                return level.equals("WARN") || level.equals("ERROR");
            case "ERROR":
                return level.equals("ERROR");
            default:
                return true;
        }
    }
    
    // Public logging methods
    
    public void logSecurity(String action, String message) {
        if (shouldLog("INFO")) {
            String fullMessage = String.format("[SECURITY] [%s] %s", action, message);
            logQueue.offer(new LogEntry("INFO", fullMessage, LocalDateTime.now()));
        }
    }
    
    public void logDebug(String message) {
        if (shouldLog("DEBUG")) {
            String fullMessage = String.format("[DEBUG] %s", message);
            logQueue.offer(new LogEntry("DEBUG", fullMessage, LocalDateTime.now()));
        }
    }
    
    public void logInfo(String message) {
        if (shouldLog("INFO")) {
            logQueue.offer(new LogEntry("INFO", message, LocalDateTime.now()));
        }
    }
    
    public void logWarning(String message) {
        if (shouldLog("WARN")) {
            logQueue.offer(new LogEntry("WARN", message, LocalDateTime.now()));
        }
    }
    
    public void logError(String message) {
        if (shouldLog("ERROR")) {
            logQueue.offer(new LogEntry("ERROR", message, LocalDateTime.now()));
        }
    }
    
    public void logError(String message, Throwable throwable) {
        if (shouldLog("ERROR")) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            
            String fullMessage = message + "\n" + sw.toString();
            logQueue.offer(new LogEntry("ERROR", fullMessage, LocalDateTime.now()));
        }
    }
    
    public void close() {
        try {
            // Process remaining logs
            processLogQueue();
            
            // Shutdown executor
            loggingExecutor.shutdown();
            
            // Close file writer
            if (fileWriter != null) {
                fileWriter.close();
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error closing logging manager: " + e.getMessage());
        }
    }
    
    // Utility methods
    
    public int getQueueSize() {
        return logQueue.size();
    }
    
    public void flushLogs() {
        processLogQueue();
    }
    
    // Inner class for log entries
    
    private static class LogEntry {
        private final String level;
        private final String message;
        private final LocalDateTime timestamp;
        
        public LogEntry(String level, String message, LocalDateTime timestamp) {
            this.level = level;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public String getLevel() {
            return level;
        }
        
        public String getMessage() {
            return message;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}