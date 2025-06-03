# Plugin Templates

This directory contains templates for different plugin configurations and integrations.

## 📁 Available Templates

### Server Type Templates
- `small-server-template.yml` - For servers with <50 players
- `medium-server-template.yml` - For servers with 50-500 players  
- `large-server-template.yml` - For servers with 500+ players
- `network-template.yml` - For BungeeCord networks

### Purpose Templates
- `creative-server-template.yml` - Creative/building servers
- `survival-server-template.yml` - Survival servers
- `minigame-server-template.yml` - Minigame networks
- `roleplay-server-template.yml` - Roleplay servers

### Security Level Templates
- `minimal-security-template.yml` - Basic protection only
- `standard-security-template.yml` - Balanced protection
- `maximum-security-template.yml` - Highest protection level
- `paranoid-security-template.yml` - Ultra-strict protection

### Integration Templates
- `discord-integration-template.yml` - Discord webhook notifications
- `database-integration-template.yml` - Database logging setup
- `api-integration-template.yml` - Custom API configurations
- `placeholder-integration-template.yml` - PlaceholderAPI setup

### Development Templates
- `testing-template.yml` - For testing environments
- `debug-template.yml` - Full debug logging
- `performance-testing-template.yml` - Performance optimization

## 🚀 Using Templates

1. **Choose appropriate template** for your server type/needs
2. **Copy to your plugins directory**:
   ```bash
   cp templates/small-server-template.yml plugins/ShadowDefenderMC/config.yml
   ```
3. **Customize settings** for your specific requirements
4. **Test configuration** with validation tools
5. **Reload plugin** to apply changes

## ⚙️ Template Customization

### Quick Customization Points

Most templates include these key sections to customize:

```yaml
# Adjust for your server size
bot_protection:
  max_joins_per_ip_per_second: 3    # Lower for smaller servers
  
# Configure based on your player base  
vpn_detection:
  action: "kick"                    # "warn" for public servers
  
# Set based on expected traffic
challenge_mode:
  trigger_threshold: 10             # Lower for private servers
```

### Server-Specific Settings

#### Small Servers (<50 players)
- Lower rate limits
- Shorter ban durations
- Warning mode for VPNs
- Higher challenge thresholds

#### Large Servers (500+ players)
- Higher rate limits
- Longer ban durations  
- Strict VPN blocking
- Lower challenge thresholds

#### Networks (BungeeCord)
- Network-wide coordination
- Higher performance settings
- Centralized logging
- Multiple API keys

## 🔧 Advanced Templates

### Multi-Server Coordination
Templates for coordinating protection across multiple servers:

- `master-server-template.yml` - Main coordination server
- `slave-server-template.yml` - Secondary servers
- `database-sync-template.yml` - Shared database configuration

### Cloud Deployment
Templates optimized for cloud environments:

- `aws-template.yml` - Amazon Web Services
- `gcp-template.yml` - Google Cloud Platform
- `azure-template.yml` - Microsoft Azure
- `docker-template.yml` - Docker containers

## 📊 Performance Templates

### High-Performance Template
```yaml
advanced:
  thread_pool_size: 8
  max_cached_ips: 50000
  use_async_processing: true
  
rate_limiting:
  max_connections_per_ip: 20
  connection_throttle: 100
```

### Low-Resource Template
```yaml
advanced:
  thread_pool_size: 2
  max_cached_ips: 1000
  cleanup_interval: 300
  
logging:
  log_level: "WARN"
  max_file_size: 5242880
```

## 🔗 Template Dependencies

Some templates require additional plugins or configurations:

- **Database templates**: Require database setup
- **Discord templates**: Require Discord webhook URLs
- **API templates**: Require API keys
- **PlaceholderAPI templates**: Require PlaceholderAPI plugin

## 📝 Creating Custom Templates

To create your own templates:

1. Start with a base template
2. Modify for your specific needs
3. Test thoroughly
4. Document any special requirements
5. Share with the community (optional)

## 🆘 Template Support

If you need help with templates:

1. Check the template documentation
2. Validate with `config-validator.py`
3. Test in development environment
4. Ask on Discord or GitHub issues