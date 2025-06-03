# API Documentation

This directory contains API documentation for ShadowDefenderMC.

## 📚 Developer API

ShadowDefenderMC provides a comprehensive API for other plugins to integrate with its protection systems.

### Getting Started

Add ShadowDefenderMC as a dependency in your plugin.yml:
```yaml
depend: [ShadowDefenderMC]
```

### Core API Classes

#### `ShadowDefenderAPI`
Main API class providing access to all protection managers.

```java
// Get the API instance
ShadowDefenderAPI api = ShadowDefenderMC.getInstance().getAPI();

// Check if a player is currently verified
boolean isVerified = api.isPlayerVerified(player);

// Manually trigger VPN check
api.checkPlayerVPN(player);
```

#### `ProtectionManager`
Manage bot protection and IP banning.

```java
ProtectionManager protection = api.getProtectionManager();

// Manually ban an IP
protection.banIP("192.168.1.100", 3600); // 1 hour

// Check if IP is whitelisted
boolean isWhitelisted = protection.isIPWhitelisted("192.168.1.100");
```

#### `ChallengeManager`
Control challenge mode and player verification.

```java
ChallengeManager challenge = api.getChallengeManager();

// Force activate challenge mode
challenge.forceActivateChallengeMode();

// Check if challenge mode is active
boolean isActive = challenge.isChallengeModeActive();
```

### Events

ShadowDefenderMC fires custom events that other plugins can listen to:

#### `PlayerVPNDetectedEvent`
Fired when a VPN is detected for a player.

```java
@EventHandler
public void onVPNDetected(PlayerVPNDetectedEvent event) {
    Player player = event.getPlayer();
    String reason = event.getReason();
    
    // Cancel the event to prevent default action
    event.setCancelled(true);
    
    // Custom handling
    player.sendMessage("Custom VPN message");
}
```

#### `BotJoinAttemptEvent`
Fired when a bot join is detected.

```java
@EventHandler
public void onBotJoin(BotJoinAttemptEvent event) {
    String ip = event.getIPAddress();
    String username = event.getUsername();
    
    // Log to custom system
    customLogger.logBotAttempt(ip, username);
}
```

#### `ChallengeModeActivatedEvent`
Fired when challenge mode is activated.

```java
@EventHandler
public void onChallengeActivated(ChallengeModeActivatedEvent event) {
    // Notify administrators
    notifyAdmins("Challenge mode activated!");
}
```

### Utility Classes

#### `IPUtil`
IP address utilities.

```java
// Check if IP is in CIDR range
boolean inRange = IPUtil.isInCIDRRange("192.168.1.100", "192.168.1.0/24");

// Get geographic location (if available)
String country = IPUtil.getCountry("8.8.8.8");
```

#### `SecurityLogger`
Custom security logging.

```java
SecurityLogger logger = api.getSecurityLogger();

// Log custom security event
logger.logSecurity("CUSTOM_EVENT", "Custom security message");
```

### Configuration API

Access and modify plugin configuration programmatically:

```java
ConfigManager config = api.getConfigManager();

// Get configuration values
int maxJoins = config.getMaxJoinsPerSecond();
boolean vpnEnabled = config.isVPNDetectionEnabled();

// Temporarily modify settings
config.setTemporaryOverride("bot_protection.enabled", false);
```

### Statistics API

Access real-time protection statistics:

```java
StatisticsManager stats = api.getStatisticsManager();

// Get current statistics
int blockedBots = stats.getBlockedBotsCount();
int blockedVPNs = stats.getBlockedVPNsCount();
Map<String, Integer> hourlyStats = stats.getHourlyStatistics();
```

### Best Practices

1. **Always check for null**: API methods may return null if the plugin is disabled
2. **Use async calls**: VPN checks and other API calls can be expensive
3. **Handle events properly**: Don't cancel events unless you're providing alternative handling
4. **Respect configuration**: Check if features are enabled before using them
5. **Thread safety**: All API methods are thread-safe

### Examples

See the `examples/` directory for complete working examples of API usage.

## 🔗 External Integrations

### PlaceholderAPI
ShadowDefenderMC supports PlaceholderAPI with the following placeholders:

- `%shadowdefender_blocked_bots%` - Total blocked bots
- `%shadowdefender_blocked_vpns%` - Total blocked VPNs
- `%shadowdefender_challenge_active%` - Challenge mode status
- `%shadowdefender_player_verified%` - Player verification status

### Discord Webhooks
Configure Discord notifications for security events:

```yaml
discord:
  enabled: true
  webhook_url: "https://discord.com/api/webhooks/..."
  events:
    - bot_blocked
    - vpn_detected
    - challenge_activated
```

### Database Support
Optional database integration for persistent statistics:

```yaml
database:
  enabled: false
  type: "mysql"  # mysql, postgresql, sqlite
  connection:
    host: "localhost"
    port: 3306
    database: "shadowdefender"
    username: "user"
    password: "pass"
```