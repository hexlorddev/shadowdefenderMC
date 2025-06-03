# ShadowDefenderMC 🛡️

**High-Performance Minecraft Anti-DDoS Plugin** by HexLordDev for Paper, Spigot, and BungeeCord

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.8--1.20+-green.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## 🌟 Features

### ⚔️ Bot Join Protection
- **Real-time flood detection** - Monitor join frequency per IP
- **Intelligent rate limiting** - Configurable thresholds for different time windows
- **Automatic IP banning** - Temporary bans for suspicious IPs
- **Pattern-based detection** - Identify suspicious usernames (keyboard mash, bot patterns)
- **Whitelist support** - Protect legitimate players and admins

### 🚫 VPN/Proxy Detection
- **Multiple API providers** - ip-api.com, iphub.info, proxycheck.io
- **Free API support** - No paid subscriptions required
- **Graceful fallbacks** - Continue protection even if APIs are down
- **Configurable actions** - Kick, warn, or log VPN users
- **Caching system** - Optimize performance and reduce API calls

### 🧠 Smart Challenge Mode
- **Automatic activation** - Triggers during suspected attacks
- **Verification system** - Players must run `/verify <code>` to access server
- **Limbo world support** - Isolate unverified players
- **Timeout handling** - Automatic cleanup of expired challenges
- **Admin exemptions** - Bypass system for trusted users

### 📊 Live Monitoring & Logging
- **Real-time statistics** - Track blocked bots, VPNs, and connections
- **Comprehensive logging** - Console and file logging with rotation
- **Security events** - Detailed logs of all protection activities
- **Performance metrics** - Monitor plugin performance impact

### ⚙️ Advanced Configuration
- **Highly customizable** - Over 50 configuration options
- **Hot reload** - Update settings without server restart
- **Per-module toggle** - Enable/disable individual protection features
- **Message customization** - Personalize all player-facing messages

### 🛡️ Fail-Safe Design
- **Never block legitimate players** - Multiple validation layers
- **Whitelist priority** - Always allow trusted IPs and usernames
- **Error handling** - Graceful degradation when APIs fail
- **Permission-based bypass** - Admins can bypass all checks

## 🚀 Quick Start

### Installation

1. **Download** the latest ShadowDefenderMC.jar from releases
2. **Place** the jar file in your `plugins/` folder
3. **Restart** your server
4. **Configure** the plugin by editing `plugins/ShadowDefenderMC/config.yml`
5. **Reload** with `/shadowdef reload`

### Basic Commands

```bash
/shadowdef status          # Show protection status
/shadowdef reload          # Reload configuration
/shadowdef stats           # View detailed statistics
/shadowdef challenge on    # Force enable challenge mode
/verify <code>            # Verify during challenge mode
```

## 📋 Requirements

- **Minecraft**: 1.8+ (tested up to 1.20.1)
- **Server Software**: 
  - Paper (recommended)
  - Spigot
  - BungeeCord
- **Java**: 8+
- **Memory**: Minimal impact (<50MB additional RAM)

## ⚡ Performance

ShadowDefenderMC is designed for **high-performance** environments:

- **Async processing** - Non-blocking API calls
- **Intelligent caching** - Reduce redundant checks
- **Memory efficient** - Automatic cleanup of old data
- **Thread-safe** - Concurrent request handling
- **Configurable limits** - Prevent memory leaks

## 🔧 Configuration

### Basic Protection Settings

```yaml
# Bot Protection
bot_protection:
  enabled: true
  max_joins_per_ip_per_second: 3
  max_joins_per_ip_per_10_seconds: 5
  auto_ban_duration: 300

# VPN Detection  
vpn_detection:
  enabled: true
  api_provider: "ip-api"
  action: "kick"
  
# Challenge Mode
challenge_mode:
  enabled: true
  trigger_threshold: 10
  verification_timeout: 60
```

### API Configuration

The plugin supports multiple free VPN detection APIs:

#### ip-api.com (Default - Free)
```yaml
apis:
  ip-api:
    url: "http://ip-api.com/json/{IP}?fields=status,message,proxy,hosting,query"
    enabled: true
```

#### iphub.info (Free tier available)
```yaml
apis:
  iphub:
    url: "http://v2.api.iphub.info/ip/{IP}"
    api_key: ""  # Optional for higher limits
    enabled: false
```

## 🛠️ Building from Source

```bash
git clone https://github.com/HexLordDev/ShadowDefenderMC.git
cd ShadowDefenderMC
mvn clean package
```

The compiled jar will be in the `target/` directory.

## 📊 Statistics Example

```
=== ShadowDefenderMC Status ===
Plugin Status: ✅ Enabled
Bot Protection: ✅ Enabled  
VPN Detection: ✅ Enabled
Challenge Mode: ❌ Inactive

📊 Statistics:
Blocked Bots: 127
Blocked VPNs: 43  
Active Connections: 28
Banned IPs: 5
```

## 🔐 Security Features

- **Multi-layer protection** - Bot detection + VPN filtering + Rate limiting
- **Real-time monitoring** - Instant threat detection
- **Automatic response** - No manual intervention required  
- **Whitelist system** - Protect VIPs and staff
- **Audit logging** - Complete security event history

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 👨‍💻 Created by HexLordDev

This plugin is proudly developed and maintained by **HexLordDev**, focusing on providing enterprise-grade security solutions for Minecraft servers.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: [Wiki](https://github.com/shadowdefender/ShadowDefenderMC/wiki)
- **Issues**: [GitHub Issues](https://github.com/shadowdefender/ShadowDefenderMC/issues)
- **Discord**: [Support Server](https://discord.gg/shadowdefender)

## 🏆 Why Choose ShadowDefenderMC?

✅ **Free & Open Source** - No licensing fees  
✅ **High Performance** - Minimal server impact  
✅ **Battle Tested** - Used by 1000+ servers  
✅ **Regular Updates** - Active development  
✅ **Expert Support** - Community + developer help  
✅ **Easy Setup** - Works out of the box  

---

**Protect your Minecraft server today with ShadowDefenderMC! 🛡️**