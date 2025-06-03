# Addons and Extensions

This directory contains additional addons and extensions for ShadowDefenderMC.

## 🧩 Available Addons

### Core Extensions
- `advanced-analytics/` - Enhanced statistics and reporting
- `geo-blocking/` - Geographic IP filtering 
- `machine-learning/` - ML-based bot detection
- `blockchain-verification/` - Blockchain-based player verification

### Integration Addons
- `discord-bot/` - Discord bot for remote management
- `web-dashboard/` - Web-based control panel
- `mobile-app/` - Mobile monitoring application
- `grafana-dashboards/` - Grafana monitoring dashboards

### Security Addons
- `honeypot/` - Honeypot system for attackers
- `forensics/` - Advanced attack analysis tools
- `threat-intelligence/` - Real-time threat feeds
- `behavioral-analysis/` - Player behavior pattern detection

### Performance Addons
- `redis-cache/` - Redis caching integration
- `cluster-sync/` - Multi-server cluster synchronization
- `load-balancer/` - Intelligent load balancing
- `auto-scaling/` - Automatic scaling based on load

### Utility Addons
- `backup-manager/` - Automated configuration backups
- `migration-tools/` - Migration from other anti-DDoS plugins
- `config-sync/` - Configuration synchronization across servers
- `audit-logger/` - Comprehensive audit logging

## 📦 Installation

### Standard Installation
1. Download addon from releases
2. Place in `plugins/ShadowDefenderMC/addons/` directory
3. Restart server or use `/shadowdef reload addons`
4. Configure addon settings if needed

### Development Installation
```bash
# Clone addon repository
git clone https://github.com/HexLordDev/ShadowDefenderMC-Addon-Name

# Build addon
cd ShadowDefenderMC-Addon-Name
mvn package

# Install to server
cp target/addon-name.jar /path/to/server/plugins/ShadowDefenderMC/addons/
```

## 🛠️ Addon Development

### Creating Custom Addons

ShadowDefenderMC supports a comprehensive addon API:

```java
@ShadowDefenderAddon(
    name = "CustomAddon",
    version = "1.0.0",
    description = "My custom addon"
)
public class CustomAddon extends AddonBase {
    
    @Override
    public void onEnable() {
        // Addon initialization
        getAPI().registerProtectionHandler(new CustomHandler());
    }
    
    @Override
    public void onDisable() {
        // Cleanup
    }
}
```

### Addon API Features
- **Event System**: Listen to protection events
- **Custom Handlers**: Implement custom protection logic
- **Data Access**: Access protection statistics and data
- **Configuration**: Manage addon-specific settings
- **Scheduling**: Schedule tasks and background operations

### Example: Simple Notification Addon

```java
public class NotificationAddon extends AddonBase {
    
    @EventHandler
    public void onBotBlocked(BotBlockedEvent event) {
        // Send notification when bot is blocked
        String message = String.format(
            "Bot blocked: %s from %s", 
            event.getUsername(), 
            event.getIPAddress()
        );
        
        sendDiscordNotification(message);
        sendEmailAlert(message);
    }
}
```

## 🔧 Configuration

Each addon can have its own configuration file in the `addons/` directory:

```yaml
# addons/advanced-analytics/config.yml
analytics:
  enabled: true
  retention_days: 30
  reports:
    daily: true
    weekly: true
    monthly: true
  
export:
  format: "json"
  destination: "reports/"
```

## 📊 Popular Addons

### Advanced Analytics
Comprehensive statistics and reporting system:
- Real-time attack visualization
- Historical trend analysis
- Custom report generation
- Performance metrics
- Export to various formats

### Discord Integration
Full Discord bot integration:
- Real-time attack notifications
- Remote server management
- Player verification system
- Statistics dashboard
- Admin command interface

### Geo-Blocking
Geographic IP filtering:
- Country-based blocking/allowing
- Region-specific rules
- ISP filtering
- Custom geographic policies
- Whitelist management

### Machine Learning
AI-powered bot detection:
- Behavioral pattern analysis
- Anomaly detection
- Adaptive learning
- False positive reduction
- Custom model training

## 🌟 Community Addons

### Contributing Addons
We welcome community-developed addons! To contribute:

1. Follow the addon development guidelines
2. Include comprehensive documentation
3. Provide example configurations
4. Submit for review via GitHub
5. Maintain compatibility with core updates

### Addon Store
Visit our addon store for the latest community addons:
- Browse by category
- Read user reviews
- Download with one click
- Automatic update notifications

## 📚 Documentation

### Addon Development Guide
- Complete API reference
- Step-by-step tutorials
- Best practices
- Example code snippets
- Testing guidelines

### Compatibility Matrix
| Addon | MC Version | Java | Dependencies |
|-------|------------|------|--------------|
| Advanced Analytics | 1.8+ | 11+ | None |
| Discord Bot | 1.8+ | 11+ | JDA |
| Geo-Blocking | 1.8+ | 8+ | MaxMind |
| Machine Learning | 1.16+ | 11+ | TensorFlow |

## 🆘 Support

### Getting Help
- Check addon documentation
- Visit Discord support channel
- Search GitHub issues
- Contact addon authors
- Community forums

### Reporting Issues
When reporting addon issues:
1. Include addon version
2. Provide error logs
3. Describe reproduction steps
4. Include configuration files
5. Test with minimal setup