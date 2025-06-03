#!/bin/bash

# Deploy to Spigot/Paper Servers
# Automatically builds and deploys the plugin

SERVER_DIR="${1:-/opt/minecraft/server}"
PLUGIN_NAME="ShadowDefenderMC-1.0.0.jar"

echo "🚀 Deploying ShadowDefenderMC to Spigot/Paper Server"
echo "================================================="

# Build the plugin
echo "🔨 Building plugin..."
./scripts/quick-build.sh

if [ $? -ne 0 ]; then
    echo "❌ Build failed, aborting deployment"
    exit 1
fi

# Check if server directory exists
if [ ! -d "$SERVER_DIR" ]; then
    echo "❌ Server directory not found: $SERVER_DIR"
    echo "Usage: $0 [server_directory]"
    exit 1
fi

# Check if plugins directory exists
if [ ! -d "$SERVER_DIR/plugins" ]; then
    echo "❌ Plugins directory not found: $SERVER_DIR/plugins"
    exit 1
fi

# Backup existing plugin if it exists
if [ -f "$SERVER_DIR/plugins/$PLUGIN_NAME" ]; then
    echo "📦 Backing up existing plugin..."
    mv "$SERVER_DIR/plugins/$PLUGIN_NAME" "$SERVER_DIR/plugins/$PLUGIN_NAME.backup.$(date +%Y%m%d_%H%M%S)"
fi

# Copy new plugin
echo "📋 Deploying plugin..."
cp "target/$PLUGIN_NAME" "$SERVER_DIR/plugins/"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Deployment successful!"
    echo "📁 Plugin deployed to: $SERVER_DIR/plugins/$PLUGIN_NAME"
    echo ""
    echo "🔄 Please restart your server or use:"
    echo "   /reload confirm (not recommended)"
    echo "   /shadowdef reload (for config changes only)"
else
    echo "❌ Deployment failed!"
    exit 1
fi