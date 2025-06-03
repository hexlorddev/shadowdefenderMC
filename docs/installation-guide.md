# ShadowDefenderMC Installation Guide

Complete step-by-step installation instructions for all server types.

## 🎯 Quick Start

### For Spigot/Paper Servers

1. **Download the Plugin**
   ```bash
   wget https://github.com/HexLordDev/ShadowDefenderMC/releases/latest/download/ShadowDefenderMC-1.0.0.jar
   ```

2. **Install the Plugin**
   ```bash
   mv ShadowDefenderMC-1.0.0.jar /path/to/your/server/plugins/
   ```

3. **Restart Your Server**
   ```bash
   # Stop server gracefully
   # Restart server
   ```

4. **Configure the Plugin**
   - Edit `plugins/ShadowDefenderMC/config.yml`
   - Customize settings for your server type
   - Use `/shadowdef reload` to apply changes

### For BungeeCord Networks

1. **Download and Install**
   ```bash
   # Place in BungeeCord plugins directory
   mv ShadowDefenderMC-1.0.0.jar /path/to/bungee/plugins/
   ```

2. **Restart BungeeCord**
   ```bash
   # BungeeCord requires full restart for new plugins
   ```

3. **Configure for Network**
   - Use network-optimized configuration
   - Coordinate with backend server settings

## 📋 System Requirements

### Minimum Requirements
- **Java**: 8 or higher
- **Server Software**: Paper 1.8+, Spigot 1.8+, or BungeeCord
- **RAM**: Additional 50MB for the plugin
- **Disk Space**: 100MB for plugin and logs

### Recommended Requirements
- **Java**: 11 or higher
- **Server Software**: Paper 1.16+ (for best performance)
- **RAM**: Additional 100MB for high-traffic servers
- **Network**: Stable internet for VPN API calls

## 🔧 Detailed Installation Steps

### Step 1: Pre-Installation Checks

```bash
# Check Java version
java -version

# Check server software and version
# Paper/Spigot: Check version command in console
# BungeeCord: Check startup logs

# Verify disk space
df -h
```

### Step 2: Download and Verification

```bash
# Create backup directory
mkdir -p backups/plugins

# Download latest release
curl -L -o ShadowDefenderMC-1.0.0.jar \
  https://github.com/HexLordDev/ShadowDefenderMC/releases/latest/download/ShadowDefenderMC-1.0.0.jar

# Verify file integrity (optional)
sha256sum ShadowDefenderMC-1.0.0.jar
```

### Step 3: Plugin Installation

```bash
# For Spigot/Paper
cp ShadowDefenderMC-1.0.0.jar /opt/minecraft/server/plugins/

# For BungeeCord
cp ShadowDefenderMC-1.0.0.jar /opt/minecraft/bungee/plugins/

# Set proper permissions
chown minecraft:minecraft /opt/minecraft/*/plugins/ShadowDefenderMC-1.0.0.jar
chmod 644 /opt/minecraft/*/plugins/ShadowDefenderMC-1.0.0.jar
```

### Step 4: First Startup

1. **Start the server** and monitor logs for any errors
2. **Verify plugin loaded** with `/plugins` command
3. **Check default configuration** was created
4. **Test basic functionality** with `/shadowdef status`

### Step 5: Initial Configuration

```yaml
# Edit plugins/ShadowDefenderMC/config.yml
bot_protection:
  enabled: true
  max_joins_per_ip_per_second: 3  # Adjust for your server

vpn_detection:
  enabled: true
  action: "warn"  # Start with warnings, then change to "kick"

challenge_mode:
  enabled: true
  trigger_threshold: 10  # Adjust based on server size
```

### Step 6: Testing and Validation

```bash
# Test basic commands
/shadowdef status
/shadowdef stats

# Monitor logs for first few hours
tail -f plugins/ShadowDefenderMC/logs/security.log

# Test with VPN (if available)
# Join with VPN to test detection
```

## 🏗️ Advanced Installation

### Docker Containers

```dockerfile
# Add to your Minecraft server Dockerfile
COPY ShadowDefenderMC-1.0.0.jar /opt/minecraft/plugins/
```

### Ansible Automation

```yaml
# playbook.yml
- name: Install ShadowDefenderMC
  get_url:
    url: "https://github.com/HexLordDev/ShadowDefenderMC/releases/latest/download/ShadowDefenderMC-1.0.0.jar"
    dest: "/opt/minecraft/server/plugins/"
    owner: minecraft
    group: minecraft
    mode: '0644'
```

### Multiple Servers

```bash
#!/bin/bash
# mass-deploy.sh
SERVERS=(
  "server1:/opt/minecraft/server1"
  "server2:/opt/minecraft/server2"
  "server3:/opt/minecraft/server3"
)

for server in "${SERVERS[@]}"; do
  IFS=':' read -r host path <<< "$server"
  scp ShadowDefenderMC-1.0.0.jar "$host:$path/plugins/"
done
```

## 🔍 Verification Steps

### Post-Installation Checklist

- [ ] Plugin appears in `/plugins` list
- [ ] Configuration file created successfully
- [ ] No errors in server console
- [ ] `/shadowdef status` command works
- [ ] Security log file being created
- [ ] VPN detection functioning (if enabled)
- [ ] Challenge mode triggers correctly (if enabled)

### Performance Verification

```bash
# Monitor server performance
top -p $(pgrep java)

# Check memory usage
/jvm
/gc

# Monitor TPS
/tps
```

### Security Testing

```bash
# Test rate limiting (carefully)
# Test VPN detection with known VPN
# Test challenge mode activation
# Monitor logs for proper event recording
```

## 🚨 Troubleshooting Installation

### Common Issues

#### Plugin Won't Load
```
Error: UnsupportedClassVersionError
Solution: Update Java to 8+ or use plugin compatible with your Java version
```

#### Configuration Errors
```
Error: Invalid YAML syntax
Solution: Validate YAML at https://yamlchecker.com/
```

#### Permission Errors
```
Error: Could not load plugin
Solution: Check file permissions and ownership
```

#### API Connection Issues
```
Error: VPN API timeout
Solution: Check internet connectivity and firewall settings
```

### Debug Mode

Enable debug logging for troubleshooting:

```yaml
plugin:
  debug: true

logging:
  log_level: "DEBUG"
```

### Getting Help

1. **Check Documentation**: Read through all docs
2. **Search Issues**: GitHub issues page
3. **Discord Support**: Join our Discord server
4. **Create Issue**: Provide full logs and configuration

## 📈 Migration from Other Plugins

### From Other Anti-Bot Plugins

1. **Backup existing configuration**
2. **Disable old plugin**
3. **Install ShadowDefenderMC**
4. **Configure similar settings**
5. **Test thoroughly**
6. **Remove old plugin**

### Configuration Mapping

```yaml
# Common setting equivalents
Other Plugin -> ShadowDefenderMC
joinLimit -> max_joins_per_ip_per_second
banTime -> auto_ban_duration
antiVPN -> vpn_detection.enabled
```

## 🎉 Successful Installation

After successful installation, you should see:

```
[INFO] ShadowDefenderMC v1.0.0 has been enabled successfully!
[INFO] Protection systems active: Bot Detection, VPN Filtering, Rate Limiting
```

Your server is now protected! Monitor the security logs and adjust configuration as needed.