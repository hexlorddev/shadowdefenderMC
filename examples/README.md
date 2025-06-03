# Example Configuration Files

This directory contains various example configurations for different server setups and use cases.

## 📁 Configuration Examples

### Public Server (`public-server-config.yml`)
- Higher join limits for community servers
- VPN warnings instead of kicks
- Longer verification timeouts
- Balanced security vs accessibility

### Private/Whitelisted Server (`private-server-config.yml`)
- Stricter protection settings
- VPN blocking enabled
- Shorter verification timeouts
- Maximum security focus

### Large Network (`network-config.yml`)
- Optimized for high player counts
- Multiple API keys for rate limiting
- Increased cache sizes
- Network-wide protection

### Development Server (`dev-server-config.yml`)
- Minimal protection for testing
- Debug logging enabled
- All checks in log-only mode
- Easy configuration for developers

## 🔧 How to Use

1. Copy your desired example to your server's `plugins/ShadowDefenderMC/` folder
2. Rename it to `config.yml`
3. Customize the settings for your specific needs
4. Reload with `/shadowdef reload`

## ⚙️ Customization Tips

- Always test configuration changes on a development server first
- Monitor logs after changes to ensure everything works correctly
- Adjust thresholds based on your server's typical traffic patterns
- Keep backup copies of working configurations