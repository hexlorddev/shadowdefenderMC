#!/bin/bash

# Deploy to BungeeCord Networks
# Builds and deploys to BungeeCord proxy servers

BUNGEE_DIR="${1:-/opt/minecraft/bungee}"
PLUGIN_NAME="ShadowDefenderMC-1.0.0.jar"

echo "🌐 Deploying ShadowDefenderMC to BungeeCord Network"
echo "==============================================="

# Build the plugin
echo "🔨 Building plugin..."
./scripts/quick-build.sh

if [ $? -ne 0 ]; then
    echo "❌ Build failed, aborting deployment"
    exit 1
fi

# Check if BungeeCord directory exists
if [ ! -d "$BUNGEE_DIR" ]; then
    echo "❌ BungeeCord directory not found: $BUNGEE_DIR"
    echo "Usage: $0 [bungee_directory]"
    exit 1
fi

# Check if plugins directory exists
if [ ! -d "$BUNGEE_DIR/plugins" ]; then
    echo "❌ Plugins directory not found: $BUNGEE_DIR/plugins"
    exit 1
fi

# Backup existing plugin if it exists
if [ -f "$BUNGEE_DIR/plugins/$PLUGIN_NAME" ]; then
    echo "📦 Backing up existing plugin..."
    mv "$BUNGEE_DIR/plugins/$PLUGIN_NAME" "$BUNGEE_DIR/plugins/$PLUGIN_NAME.backup.$(date +%Y%m%d_%H%M%S)"
fi

# Copy new plugin
echo "📋 Deploying plugin..."
cp "target/$PLUGIN_NAME" "$BUNGEE_DIR/plugins/"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ BungeeCord deployment successful!"
    echo "📁 Plugin deployed to: $BUNGEE_DIR/plugins/$PLUGIN_NAME"
    echo ""
    echo "🔄 Please restart your BungeeCord proxy"
    echo "   BungeeCord does not support hot reloading"
else
    echo "❌ Deployment failed!"
    exit 1
fi