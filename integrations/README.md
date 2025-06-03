# Integrations and Plugin Compatibility

This directory contains integration guides and compatibility modules for various plugins and systems.

## 🔗 Supported Integrations

### Chat and Communication
- **Discord Integrations** - Webhooks, bot commands, notifications
- **Slack Integration** - Enterprise team notifications
- **Microsoft Teams** - Corporate environment alerts
- **Telegram Bot** - Mobile-first notifications

### Authentication Systems
- **AuthMe Integration** - Enhanced login protection
- **LoginSecurity** - Dual-layer security
- **nLogin** - Premium account verification
- **TwoFactorAuth** - 2FA integration

### Monitoring and Analytics
- **PlaceholderAPI** - Statistics placeholders
- **Grafana Dashboards** - Professional monitoring
- **Prometheus Metrics** - Time-series data
- **ELK Stack** - Log aggregation and analysis

### Database Systems
- **MySQL/MariaDB** - Primary database support
- **PostgreSQL** - Advanced analytics
- **MongoDB** - NoSQL document storage
- **Redis** - High-performance caching

### Cloud Services
- **AWS CloudWatch** - Cloud monitoring
- **Google Cloud Logging** - Centralized logs
- **Azure Monitor** - Enterprise monitoring
- **DigitalOcean Monitoring** - VPS tracking

### Security Plugins
- **CoreProtect** - Block logging integration
- **WorldGuard** - Region-based protection
- **Essentials** - Ban system integration
- **AdvancedBan** - Enhanced punishment system

## 📦 Installation Guides

### Discord Integration Setup

1. **Create Discord Application**
   ```
   https://discord.com/developers/applications
   ```

2. **Configure Webhook**
   ```yaml
   discord:
     enabled: true
     webhook_url: "https://discord.com/api/webhooks/YOUR_WEBHOOK"
     events:
       - bot_blocked
       - vpn_detected
       - challenge_activated
   ```

3. **Bot Commands Setup**
   ```yaml
   discord_bot:
     token: "YOUR_BOT_TOKEN"
     commands:
       status: true
       statistics: true
       ban_ip: true
   ```

### Database Integration

1. **MySQL Setup**
   ```sql
   CREATE DATABASE shadowdefender;
   CREATE USER 'shadowdef'@'localhost' IDENTIFIED BY 'secure_password';
   GRANT ALL PRIVILEGES ON shadowdefender.* TO 'shadowdef'@'localhost';
   ```

2. **Plugin Configuration**
   ```yaml
   database:
     enabled: true
     type: mysql
     host: localhost
     port: 3306
     database: shadowdefender
     username: shadowdef
     password: secure_password
   ```

### PlaceholderAPI Setup

1. **Install PlaceholderAPI**
   ```bash
   /papi download ShadowDefenderMC
   /papi reload
   ```

2. **Available Placeholders**
   ```
   %shadowdefender_blocked_bots%
   %shadowdefender_blocked_vpns%
   %shadowdefender_challenge_active%
   %shadowdefender_player_verified%
   %shadowdefender_protection_level%
   ```

## 🛠️ Custom Integrations

### API Integration Example

```java
// Custom plugin integration
@Plugin(name = "CustomIntegration")
public class CustomIntegration extends JavaPlugin {
    
    @EventHandler
    public void onBotBlocked(BotBlockedEvent event) {
        // Custom handling for bot blocks
        sendToCustomSystem(event.getIPAddress(), event.getReason());
    }
    
    private void sendToCustomSystem(String ip, String reason) {
        // Your custom integration logic
    }
}
```

### Webhook Integration

```python
# Python webhook receiver
from flask import Flask, request
import json

app = Flask(__name__)

@app.route('/shadowdefender-webhook', methods=['POST'])
def handle_webhook():
    data = request.json
    event_type = data.get('event_type')
    
    if event_type == 'bot_blocked':
        # Handle bot blocking event
        handle_bot_block(data)
    elif event_type == 'vpn_detected':
        # Handle VPN detection
        handle_vpn_detection(data)
    
    return 'OK'
```

## 📊 Monitoring Integrations

### Grafana Dashboard Setup

1. **Install Grafana Plugin**
   ```bash
   grafana-cli plugins install grafana-simple-json-datasource
   ```

2. **Configure Data Source**
   ```json
   {
     "name": "ShadowDefenderMC",
     "type": "simplejson",
     "url": "http://your-server:8080/api/shadowdefender",
     "access": "proxy"
   }
   ```

3. **Import Dashboard**
   ```
   Dashboard ID: shadowdefender-security-metrics
   ```

### Prometheus Metrics

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'shadowdefender'
    static_configs:
      - targets: ['your-server:9090']
    metrics_path: '/metrics/shadowdefender'
```

## 🌐 Network Integrations

### BungeeCord Network Setup

1. **Install on Proxy**
   ```bash
   # Place plugin in BungeeCord plugins/
   cp ShadowDefenderMC.jar bungee/plugins/
   ```

2. **Configure Network Mode**
   ```yaml
   network:
     mode: "proxy"
     backend_sync: true
     shared_database: true
   ```

3. **Backend Server Config**
   ```yaml
   network:
     mode: "backend"
     proxy_ip: "192.168.1.100"
     trust_proxy: true
   ```

### Velocity Support

```java
// Velocity plugin integration
@Plugin(id = "shadowdefender-velocity")
public class ShadowDefenderVelocity {
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Initialize Velocity integration
    }
}
```

## 🔧 Development Integration

### Maven Dependency

```xml
<dependency>
    <groupId>com.hexlorddev</groupId>
    <artifactId>shadowdefender-api</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Gradle Dependency

```gradle
dependencies {
    compileOnly 'com.hexlorddev:shadowdefender-api:1.0.0'
}
```

## 📚 Integration Examples

Each integration folder contains:
- Complete setup guides
- Configuration examples
- Code samples
- Troubleshooting tips
- Performance considerations

## 🆘 Integration Support

For integration help:
1. Check the specific integration folder
2. Review API documentation
3. Test with minimal configuration
4. Contact HexLordDev support
5. Community Discord server