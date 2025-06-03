#!/usr/bin/env python3
"""
Configuration Validator for ShadowDefenderMC
Validates YAML configuration files for syntax and logical errors.
"""

import sys
import yaml
import argparse
from pathlib import Path

class ConfigValidator:
    def __init__(self):
        self.errors = []
        self.warnings = []
        
    def validate_file(self, config_path):
        """Validate a configuration file."""
        try:
            with open(config_path, 'r') as file:
                config = yaml.safe_load(file)
        except yaml.YAMLError as e:
            self.errors.append(f"YAML syntax error: {e}")
            return False
        except FileNotFoundError:
            self.errors.append(f"Configuration file not found: {config_path}")
            return False
            
        return self.validate_config(config)
    
    def validate_config(self, config):
        """Validate configuration structure and values."""
        if not isinstance(config, dict):
            self.errors.append("Configuration must be a dictionary")
            return False
            
        # Validate plugin section
        self.validate_plugin_section(config.get('plugin', {}))
        
        # Validate bot protection
        self.validate_bot_protection(config.get('bot_protection', {}))
        
        # Validate VPN detection
        self.validate_vpn_detection(config.get('vpn_detection', {}))
        
        # Validate challenge mode
        self.validate_challenge_mode(config.get('challenge_mode', {}))
        
        # Validate rate limiting
        self.validate_rate_limiting(config.get('rate_limiting', {}))
        
        # Validate logging
        self.validate_logging(config.get('logging', {}))
        
        # Validate advanced settings
        self.validate_advanced(config.get('advanced', {}))
        
        return len(self.errors) == 0
    
    def validate_plugin_section(self, plugin):
        """Validate plugin configuration section."""
        if not isinstance(plugin, dict):
            self.errors.append("plugin section must be a dictionary")
            return
            
        enabled = plugin.get('enabled', True)
        if not isinstance(enabled, bool):
            self.errors.append("plugin.enabled must be boolean")
    
    def validate_bot_protection(self, bot_protection):
        """Validate bot protection settings."""
        if not isinstance(bot_protection, dict):
            return
            
        # Check rate limits
        max_per_second = bot_protection.get('max_joins_per_ip_per_second', 3)
        max_per_10s = bot_protection.get('max_joins_per_ip_per_10_seconds', 5)
        
        if not isinstance(max_per_second, int) or max_per_second < 1:
            self.errors.append("max_joins_per_ip_per_second must be positive integer")
            
        if not isinstance(max_per_10s, int) or max_per_10s < max_per_second:
            self.errors.append("max_joins_per_ip_per_10_seconds must be >= max_joins_per_ip_per_second")
            
        # Check ban duration
        ban_duration = bot_protection.get('auto_ban_duration', 300)
        if not isinstance(ban_duration, int) or ban_duration < 30:
            self.warnings.append("auto_ban_duration should be at least 30 seconds")
    
    def validate_vpn_detection(self, vpn_detection):
        """Validate VPN detection settings.""" 
        if not isinstance(vpn_detection, dict):
            return
            
        api_provider = vpn_detection.get('api_provider', 'ip-api')
        valid_providers = ['ip-api', 'iphub', 'proxycheck']
        
        if api_provider not in valid_providers:
            self.errors.append(f"api_provider must be one of: {valid_providers}")
            
        timeout = vpn_detection.get('timeout', 5000)
        if not isinstance(timeout, int) or timeout < 1000:
            self.warnings.append("VPN API timeout should be at least 1000ms")
            
        action = vpn_detection.get('action', 'kick')
        valid_actions = ['kick', 'warn', 'log']
        if action not in valid_actions:
            self.errors.append(f"VPN action must be one of: {valid_actions}")
    
    def validate_challenge_mode(self, challenge_mode):
        """Validate challenge mode settings."""
        if not isinstance(challenge_mode, dict):
            return
            
        threshold = challenge_mode.get('trigger_threshold', 10)
        if not isinstance(threshold, int) or threshold < 5:
            self.warnings.append("trigger_threshold should be at least 5")
            
        timeout = challenge_mode.get('verification_timeout', 60)
        if not isinstance(timeout, int) or timeout < 30:
            self.warnings.append("verification_timeout should be at least 30 seconds")
    
    def validate_rate_limiting(self, rate_limiting):
        """Validate rate limiting settings."""
        if not isinstance(rate_limiting, dict):
            return
            
        max_packets = rate_limiting.get('max_packets_per_second', 100)
        if not isinstance(max_packets, int) or max_packets < 10:
            self.warnings.append("max_packets_per_second seems very low")
            
        max_connections = rate_limiting.get('max_connections_per_ip', 5)
        if not isinstance(max_connections, int) or max_connections < 1:
            self.errors.append("max_connections_per_ip must be at least 1")
    
    def validate_logging(self, logging):
        """Validate logging settings."""
        if not isinstance(logging, dict):
            return
            
        log_level = logging.get('log_level', 'INFO')
        valid_levels = ['DEBUG', 'INFO', 'WARN', 'ERROR']
        if log_level not in valid_levels:
            self.errors.append(f"log_level must be one of: {valid_levels}")
            
        max_size = logging.get('max_file_size', 10485760)
        if not isinstance(max_size, int) or max_size < 1048576:
            self.warnings.append("max_file_size should be at least 1MB")
    
    def validate_advanced(self, advanced):
        """Validate advanced settings."""
        if not isinstance(advanced, dict):
            return
            
        thread_pool_size = advanced.get('thread_pool_size', 4)
        if not isinstance(thread_pool_size, int) or thread_pool_size < 1:
            self.errors.append("thread_pool_size must be at least 1")
            
        if thread_pool_size > 16:
            self.warnings.append("thread_pool_size > 16 may not improve performance")
            
        cache_duration = advanced.get('cache_duration', 300)
        if not isinstance(cache_duration, int) or cache_duration < 60:
            self.warnings.append("cache_duration should be at least 60 seconds")
    
    def print_results(self):
        """Print validation results."""
        if self.errors:
            print("🚨 ERRORS:")
            for error in self.errors:
                print(f"  ❌ {error}")
            print()
            
        if self.warnings:
            print("⚠️  WARNINGS:")
            for warning in self.warnings:
                print(f"  ⚠️  {warning}")
            print()
            
        if not self.errors and not self.warnings:
            print("✅ Configuration is valid!")
        elif not self.errors:
            print("✅ Configuration is valid (with warnings)")
        else:
            print("❌ Configuration has errors!")
            
        return len(self.errors) == 0

def main():
    parser = argparse.ArgumentParser(description='Validate ShadowDefenderMC configuration')
    parser.add_argument('config', help='Path to configuration file')
    parser.add_argument('--strict', action='store_true', help='Treat warnings as errors')
    
    args = parser.parse_args()
    
    validator = ConfigValidator()
    
    print(f"🔍 Validating configuration: {args.config}")
    print("=" * 50)
    
    is_valid = validator.validate_file(args.config)
    validator.print_results()
    
    if args.strict and validator.warnings:
        is_valid = False
        
    sys.exit(0 if is_valid else 1)

if __name__ == '__main__':
    main()