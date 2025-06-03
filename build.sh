#!/bin/bash

# ShadowDefenderMC Build Script
# This script builds the plugin using Maven

echo "🛡️ ShadowDefenderMC Build Script"
echo "================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed or not in PATH"
    echo "Please install Maven to build the plugin"
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    echo "Please install Java 8+ to build the plugin"
    exit 1
fi

echo "📋 Environment Check:"
echo "Maven version: $(mvn -version | head -1)"
echo "Java version: $(java -version 2>&1 | head -1)"
echo ""

# Clean previous builds
echo "🧹 Cleaning previous builds..."
mvn clean

# Compile and package
echo "🔨 Building ShadowDefenderMC..."
mvn package

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build completed successfully!"
    echo ""
    echo "📦 Generated files:"
    if [ -f "target/ShadowDefenderMC-1.0.0.jar" ]; then
        echo "   - target/ShadowDefenderMC-1.0.0.jar (Main plugin)"
        echo ""
        echo "🚀 Installation Instructions:"
        echo "1. Copy target/ShadowDefenderMC-1.0.0.jar to your server's plugins/ folder"
        echo "2. Restart your server"
        echo "3. Configure the plugin in plugins/ShadowDefenderMC/config.yml"
        echo "4. Use /shadowdef reload to apply changes"
        echo ""
        echo "📊 Plugin Features:"
        echo "   ⚔️  Bot Join Protection"
        echo "   🚫 VPN/Proxy Detection"
        echo "   🧠 Smart Challenge Mode"
        echo "   📊 Live Monitoring & Logging"
        echo "   ⚙️  Advanced Configuration"
        echo ""
        echo "For support and documentation, visit:"
        echo "https://github.com/shadowdefender/ShadowDefenderMC"
    else
        echo "⚠️ Plugin jar not found in target/ directory"
    fi
else
    echo ""
    echo "❌ Build failed!"
    echo "Please check the error messages above and fix any issues."
    echo ""
    echo "Common issues:"
    echo "- Missing dependencies (check pom.xml)"
    echo "- Java version compatibility"
    echo "- Network issues downloading dependencies"
    exit 1
fi