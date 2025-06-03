#!/bin/bash

# Quick Build Script for ShadowDefenderMC
# Performs fast compilation without running tests

echo "🚀 ShadowDefenderMC Quick Build"
echo "==============================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed"
    exit 1
fi

# Navigate to project directory
cd "$(dirname "$0")/.."

echo "📦 Building without tests..."
mvn clean compile package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Quick build completed!"
    echo "📁 JAR location: target/ShadowDefenderMC-1.0.0.jar"
else
    echo ""
    echo "❌ Build failed!"
    exit 1
fi