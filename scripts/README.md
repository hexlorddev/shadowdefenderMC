# ShadowDefenderMC Scripts and Utilities

This directory contains useful scripts for managing and deploying ShadowDefenderMC.

## 📁 Available Scripts

### Build Scripts
- `build.sh` - Main build script (located in root directory)
- `quick-build.sh` - Fast compilation without tests
- `release-build.sh` - Production build with optimization

### Deployment Scripts
- `deploy-spigot.sh` - Deploy to Spigot/Paper servers
- `deploy-bungee.sh` - Deploy to BungeeCord networks
- `deploy-all.sh` - Deploy to all server types

### Maintenance Scripts
- `update-dependencies.sh` - Update Maven dependencies
- `clean-logs.sh` - Clean up old log files
- `backup-config.sh` - Backup configuration files

### Development Scripts
- `setup-dev.sh` - Set up development environment
- `run-tests.sh` - Run all unit tests
- `generate-docs.sh` - Generate documentation

### Monitoring Scripts
- `monitor-performance.sh` - Monitor plugin performance
- `analyze-logs.sh` - Analyze security logs
- `health-check.sh` - Server health monitoring

## 🔧 Usage Instructions

1. Make scripts executable: `chmod +x scripts/*.sh`
2. Run from project root: `./scripts/script-name.sh`
3. Some scripts may require configuration variables
4. Check script comments for specific requirements

## ⚠️ Notes

- Always test scripts on development servers first
- Some scripts require sudo privileges
- Backup important data before running deployment scripts
- Scripts are designed for Linux/Unix environments