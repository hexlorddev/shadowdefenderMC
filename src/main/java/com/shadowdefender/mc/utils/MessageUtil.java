package com.shadowdefender.mc.utils;

import org.bukkit.ChatColor;

public class MessageUtil {
    
    /**
     * Converts color codes in a string to Minecraft color codes
     * Supports both & and § color codes
     */
    public static String colorize(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Removes all color codes from a string
     */
    public static String stripColors(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.stripColor(colorize(message));
    }
    
    /**
     * Converts a message array to a single colored string
     */
    public static String colorize(String[] messages) {
        if (messages == null || messages.length == 0) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < messages.length; i++) {
            builder.append(colorize(messages[i]));
            if (i < messages.length - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
    
    /**
     * Creates a centered message with padding
     */
    public static String center(String message, int length, char padding) {
        if (message == null) {
            message = "";
        }
        
        String stripped = stripColors(message);
        if (stripped.length() >= length) {
            return colorize(message);
        }
        
        int totalPadding = length - stripped.length();
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < leftPadding; i++) {
            builder.append(padding);
        }
        builder.append(colorize(message));
        for (int i = 0; i < rightPadding; i++) {
            builder.append(padding);
        }
        
        return builder.toString();
    }
    
    /**
     * Creates a formatted header with borders
     */
    public static String createHeader(String title, int width) {
        StringBuilder builder = new StringBuilder();
        
        // Top border
        builder.append("&6");
        for (int i = 0; i < width; i++) {
            builder.append("=");
        }
        builder.append("\n");
        
        // Title line
        builder.append(center("&e&l" + title, width, ' '));
        builder.append("\n");
        
        // Bottom border
        builder.append("&6");
        for (int i = 0; i < width; i++) {
            builder.append("=");
        }
        
        return colorize(builder.toString());
    }
    
    /**
     * Formats a time duration in seconds to a human-readable string
     */
    public static String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + " second" + (seconds == 1 ? "" : "s");
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            String result = minutes + " minute" + (minutes == 1 ? "" : "s");
            if (remainingSeconds > 0) {
                result += " " + remainingSeconds + " second" + (remainingSeconds == 1 ? "" : "s");
            }
            return result;
        } else {
            long hours = seconds / 3600;
            long remainingMinutes = (seconds % 3600) / 60;
            String result = hours + " hour" + (hours == 1 ? "" : "s");
            if (remainingMinutes > 0) {
                result += " " + remainingMinutes + " minute" + (remainingMinutes == 1 ? "" : "s");
            }
            return result;
        }
    }
    
    /**
     * Formats a number with thousands separators
     */
    public static String formatNumber(long number) {
        return String.format("%,d", number);
    }
    
    /**
     * Creates a progress bar
     */
    public static String createProgressBar(double percentage, int length, char filled, char empty) {
        int filledChars = (int) Math.round((percentage / 100.0) * length);
        
        StringBuilder builder = new StringBuilder();
        builder.append("&a");
        for (int i = 0; i < filledChars; i++) {
            builder.append(filled);
        }
        builder.append("&7");
        for (int i = filledChars; i < length; i++) {
            builder.append(empty);
        }
        
        return colorize(builder.toString());
    }
    
    /**
     * Wraps text to a specific line length
     */
    public static String[] wrapText(String text, int lineLength) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }
        
        String[] words = text.split(" ");
        if (words.length == 0) {
            return new String[]{text};
        }
        
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String strippedWord = stripColors(word);
            String strippedCurrentLine = stripColors(currentLine.toString());
            
            if (strippedCurrentLine.length() + strippedWord.length() + 1 <= lineLength) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }
}