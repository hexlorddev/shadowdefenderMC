# ShadowDefenderMC Configuration Guide

This guide covers all configuration options available in ShadowDefenderMC.

## 📁 Configuration File Location

The main configuration file is located at:
```
plugins/ShadowDefenderMC/config.yml
```

After making changes, reload with: `/shadowdef reload`

## 🔧 Configuration Sections

### General Plugin Settings

```yaml
plugin:
  enabled: true          # Master enable/disable switch
  debug: false          # Enable debug logging
  update_check: true    # Check for plugin updates
```

### ⚔️ Bot Protection

```yaml
bot_protection:
  enabled: true
  max_joins_per_ip_per_second: 3      # Max connections per IP per second
  max_joins_per_ip_per_10_seconds: 5  # Max connections per IP per 10 seconds  
  auto_ban_duration: 300              # Ban duration in seconds (5 minutes)
  
  # Regex patterns to detect suspicious usernames
  suspicious_username_patterns:
    - "^[a-z]{1,3}[0-9]{3,8}$"           # abc123, x456789
    - "^[qwertyuiopasdfghjklzxcvbnm]{4,12}$"  # keyboard mash
    - "^(test|bot|hack|ddos|spam)[0-9]*$"     # common bot names
    
  # Usernames that bypass all checks
  whitelist_usernames:
    - "AdminUser"
    - "ServerOwner"
```

### 🚫 VPN/Proxy Detection

```yaml
vpn_detection:
  enabled: true
  api_provider: "ip-api"     # Options: ip-api, iphub, proxycheck
  timeout: 5000             # API timeout in milliseconds
  action: "kick"            # Options: kick, warn, log
  message: "&cVPN/Proxy connections are not allowed on this server!"
  
  # IPs that bypass VPN checks (supports CIDR notation)
  whitelist_ips:
    - "127.0.0.1"
    - "192.168.1.0/24"
    - "10.0.0.0/8"
```

### 🌐 API Provider Configuration

#### ip-api.com (Free, Recommended)
```yaml
apis:
  ip-api:
    url: "http://ip-api.com/json/{IP}?fields=status,message,proxy,hosting,query"
    enabled: true
```

#### iphub.info (Free tier + Paid)
```yaml
apis:
  iphub:
    url: "http://v2.api.iphub.info/ip/{IP}"
    api_key: "your_api_key_here"  # Optional, increases rate limits
    enabled: false
```

#### proxycheck.io (Free tier + Paid)
```yaml
apis:
  proxycheck:
    url: "http://proxycheck.io/v2/{IP}?vpn=1&asn=1"
    api_key: "your_api_key_here"  # Optional
    enabled: false
```

### 🧠 Smart Challenge Mode

```yaml
challenge_mode:
  enabled: true
  trigger_threshold: 10        # Suspicious joins to activate challenge mode
  duration: 300               # How long challenge mode stays active (seconds)
  verification_timeout: 60    # Time players have to verify (seconds)
  limbo_world: "limbo"        # World for unverified players
  
  welcome_message: |
    &e&lSERVER PROTECTION ACTIVE
    &7Please verify yourself by typing:
    &a/verify {CODE}
    &7Your verification code: &b{CODE}
    
  kick_message: "&cVerification timeout! Please rejoin and verify quickly."
```

### 🛡️ Rate Limiting

```yaml
rate_limiting:
  enabled: true
  max_packets_per_second: 100    # Packet rate limit per IP
  max_connections_per_ip: 5      # Max concurrent connections per IP
  connection_throttle: 1000      # Min time between connections (ms)
```

### 📊 Logging Configuration

```yaml
logging:
  console: true                                           # Log to console
  file: true                                             # Log to file
  file_path: "plugins/ShadowDefenderMC/logs/security.log"
  max_file_size: 10485760                               # 10MB
  log_level: "INFO"                                     # DEBUG, INFO, WARN, ERROR
```

### 💬 Custom Messages

```yaml
messages:
  prefix: "&8[&6ShadowDefender&8] "
  bot_kick: "&cConnection blocked: Suspicious activity detected"
  rate_limit_kick: "&cConnection blocked: Too many requests"  
  vpn_kick: "&cVPN/Proxy connections are not allowed"
  verification_success: "&aVerification successful! Welcome to the server."
  verification_failed: "&cInvalid verification code!"
  challenge_activated: "&eServer protection activated due to suspicious activity"
```

### ⚙️ Advanced Settings

```yaml
advanced:
  use_async_processing: true    # Enable async API calls
  cache_duration: 300          # IP cache duration (seconds)
  cleanup_interval: 60         # Cleanup task interval (seconds)
  max_cached_ips: 10000       # Maximum cached IP addresses
  thread_pool_size: 4         # Number of async threads
```

## 🎯 Configuration Examples

### High Security Server
```yaml
bot_protection:
  max_joins_per_ip_per_second: 1
  max_joins_per_ip_per_10_seconds: 2
  auto_ban_duration: 600

vpn_detection:
  action: "kick"
  
challenge_mode:
  trigger_threshold: 5
  verification_timeout: 30
```

### Public/Community Server
```yaml
bot_protection:
  max_joins_per_ip_per_second: 5
  max_joins_per_ip_per_10_seconds: 8
  auto_ban_duration: 180

vpn_detection:
  action: "warn"
  
challenge_mode:
  trigger_threshold: 15
  verification_timeout: 90
```

### Development/Testing Server
```yaml
bot_protection:
  enabled: false
  
vpn_detection:
  action: "log"
  
challenge_mode:
  enabled: false
```

## 🔍 Regex Pattern Examples

### Suspicious Username Patterns

```yaml
suspicious_username_patterns:
  # Random letters + numbers (abc123, xy789)
  - "^[a-z]{1,3}[0-9]{3,8}$"
  
  # Pure keyboard mash (asdfgh, qwerty)
  - "^[qwertyuiopasdfghjklzxcvbnm]{4,12}$"
  
  # Common bot prefixes
  - "^(test|bot|hack|ddos|spam|fake)[0-9]*$"
  
  # Sequential patterns (user1, user2, user3)
  - "^user[0-9]{1,4}$"
  
  # Random uppercase/lowercase mix
  - "^[A-Za-z]{1,2}[0-9]{4,8}[A-Za-z]{1,2}$"
```

## 🌍 IP Whitelist Examples

```yaml
whitelist_ips:
  # Localhost
  - "127.0.0.1"
  - "::1"
  
  # Private networks
  - "192.168.0.0/16"    # Home networks
  - "10.0.0.0/8"        # Corporate networks
  - "172.16.0.0/12"     # Docker networks
  
  # Specific trusted IPs
  - "1.2.3.4"           # Admin's IP
  - "5.6.7.8"           # Staff member's IP
  
  # CloudFlare IPs (if using CloudFlare proxy)
  - "103.21.244.0/22"
  - "103.22.200.0/22"
```

## 📈 Performance Tuning

### High Traffic Servers (1000+ players)
```yaml
advanced:
  thread_pool_size: 8
  max_cached_ips: 50000
  cleanup_interval: 30

rate_limiting:
  max_connections_per_ip: 10
  connection_throttle: 500
```

### Low Resource Servers
```yaml
advanced:
  thread_pool_size: 2
  max_cached_ips: 5000
  cleanup_interval: 120

vpn_detection:
  timeout: 3000
```

## 🔧 Troubleshooting

### Common Issues

1. **VPN API not working**
   ```yaml
   vpn_detection:
     timeout: 10000  # Increase timeout
     action: "log"   # Change to log-only while debugging
   ```

2. **Too many false positives**
   ```yaml
   bot_protection:
     max_joins_per_ip_per_second: 5  # Increase limits
     suspicious_username_patterns: []  # Disable pattern matching
   ```

3. **Performance issues**
   ```yaml
   advanced:
     use_async_processing: true
     thread_pool_size: 4
   
   vpn_detection:
     enabled: false  # Temporarily disable VPN checks
   ```

## 📋 Validation

The plugin automatically validates your configuration on startup and reload. Common validation errors:

- **Rate limits too low**: Minimum 1 join per second
- **Invalid regex patterns**: Will be skipped with a warning
- **Unknown API provider**: Will fallback to ip-api
- **Invalid timeout values**: Will use defaults

## 🔄 Hot Reload

Most configuration changes can be applied without restarting:

```bash
/shadowdef reload
```

**Note**: Some changes require a full restart:
- Thread pool size changes
- Major structural changes
- Plugin enable/disable

## 📞 Support

If you need help with configuration:

1. Check the console for validation errors
2. Enable debug mode: `plugin.debug: true`
3. Join our Discord support server
4. Create an issue on GitHub with your config file

---

**Remember**: Always test configuration changes on a development server first! 🧪