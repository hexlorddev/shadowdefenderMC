# Configuration Templates

Pre-configured templates for different server environments and use cases.

## Available Templates

### Server Types
- `vanilla-server.yml` - Basic protection for vanilla servers
- `modded-server.yml` - Enhanced protection for modded servers
- `pvp-server.yml` - PvP-focused protection settings
- `creative-server.yml` - Creative server optimized settings
- `survival-server.yml` - Survival server protection
- `mini-games.yml` - Mini-game server configuration

### Network Types
- `small-network.yml` - Small server networks (2-5 servers)
- `medium-network.yml` - Medium networks (5-20 servers)
- `large-network.yml` - Large networks (20+ servers)
- `proxy-only.yml` - Proxy-only protection

### Security Levels
- `minimal.yml` - Basic protection with minimal false positives
- `balanced.yml` - Balanced protection and performance
- `strict.yml` - Maximum protection (may have false positives)
- `paranoid.yml` - Ultra-strict protection for high-risk servers

### Special Cases
- `event-server.yml` - Large event hosting configuration
- `whitelist-only.yml` - Whitelist-only server protection
- `development.yml` - Development environment settings
- `testing.yml` - Testing environment configuration

## Usage
1. Copy the appropriate template to your plugin's config directory
2. Rename it to `config.yml`
3. Modify settings as needed for your specific requirements
4. Reload the plugin to apply changes

---
**ShadowDefenderMC** - Created by HexLordDev