package com.shadowdefender.mc.commands;

import com.shadowdefender.mc.ShadowDefenderMC;
import com.shadowdefender.mc.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VerifyCommand implements CommandExecutor {
    
    private final ShadowDefenderMC plugin;
    
    public VerifyCommand(ShadowDefenderMC plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Only players can use this command
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.colorize("&cThis command can only be used by players!"));
            return true;
        }
        
        Player player = (Player) sender;
        String prefix = plugin.getConfigManager().getPrefix();
        
        // Check if player has permission
        if (!player.hasPermission("shadowdefender.verify")) {
            player.sendMessage(MessageUtil.colorize(prefix + "&cYou don't have permission to use this command!"));
            return true;
        }
        
        // Check if verification is needed
        if (!plugin.getChallengeManager().isChallengeModeActive()) {
            player.sendMessage(MessageUtil.colorize(prefix + "&eServer is not currently in challenge mode."));
            return true;
        }
        
        // Check if player is already verified
        if (plugin.getChallengeManager().isPlayerVerified(player)) {
            player.sendMessage(MessageUtil.colorize(prefix + "&aYou are already verified!"));
            return true;
        }
        
        // Check if player has a pending challenge
        if (!plugin.getChallengeManager().isPlayerChallenged(player)) {
            player.sendMessage(MessageUtil.colorize(prefix + "&eYou don't have a pending verification challenge."));
            return true;
        }
        
        // Check if verification code was provided
        if (args.length == 0) {
            player.sendMessage(MessageUtil.colorize(prefix + "&eUsage: /verify <code>"));
            player.sendMessage(MessageUtil.colorize("&7Please enter the verification code shown when you joined."));
            return true;
        }
        
        String inputCode = args[0];
        
        // Validate code format (should be 4 digits)
        if (!inputCode.matches("\\d{4}")) {
            player.sendMessage(MessageUtil.colorize(prefix + "&cInvalid code format! Code should be 4 digits."));
            return true;
        }
        
        // Attempt verification
        boolean verified = plugin.getChallengeManager().verifyPlayer(player, inputCode);
        
        if (verified) {
            // Success message is sent by ChallengeManager
            plugin.getLoggingManager().logSecurity("VERIFICATION_SUCCESS", 
                String.format("Player successfully verified: %s", player.getName()));
        } else {
            player.sendMessage(MessageUtil.colorize(prefix + 
                plugin.getConfigManager().getMessage("verification_failed")));
            
            plugin.getLoggingManager().logSecurity("VERIFICATION_FAILED", 
                String.format("Player failed verification: %s (code: %s)", player.getName(), inputCode));
        }
        
        return true;
    }
}