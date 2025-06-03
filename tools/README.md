# Tools and Utilities

This directory contains additional tools and utilities for ShadowDefenderMC development and deployment.

## 🛠️ Available Tools

### Database Tools
- `database-setup.sql` - Database schema for statistics storage
- `migration-tools/` - Database migration scripts
- `backup-restore/` - Database backup and restore utilities

### Configuration Tools
- `config-validator.py` - Validate configuration files
- `config-generator.py` - Generate configurations for different scenarios
- `settings-migrator.sh` - Migrate settings between versions

### Testing Tools
- `load-tester.jar` - Load testing tool for protection systems
- `api-tester.py` - Test VPN detection APIs
- `bot-simulator.py` - Simulate bot attacks for testing

### Monitoring Tools
- `performance-monitor.sh` - Real-time performance monitoring
- `log-analyzer.py` - Analyze security logs for patterns
- `alert-system.py` - Set up automated alerts

### Development Tools
- `dev-setup.sh` - Set up development environment
- `code-formatter.xml` - IDE code formatting settings
- `pre-commit-hooks/` - Git pre-commit hooks

### Deployment Tools
- `docker/` - Docker containers for testing
- `kubernetes/` - Kubernetes deployment manifests
- `ansible/` - Ansible playbooks for deployment

## 📦 Usage

Each tool includes its own documentation and usage instructions. Most tools are cross-platform and work on Linux, macOS, and Windows.

### Prerequisites

- Python 3.8+ (for Python tools)
- Java 11+ (for Java tools)
- Bash (for shell scripts)
- Docker (for containerized tools)

### Getting Started

1. Check tool-specific requirements
2. Run `./tools/setup.sh` for automatic setup
3. Use `./tools/help.sh` for tool overview
4. Each tool has `--help` option for detailed usage

## 🔧 Tool Categories

### Production Tools
Tools ready for production environments with full error handling and logging.

### Development Tools
Tools for developers working on ShadowDefenderMC or integrating with it.

### Experimental Tools
Beta tools under development - use with caution in production.

## 🤝 Contributing Tools

We welcome contributions of new tools! Please follow these guidelines:

1. Include comprehensive documentation
2. Add error handling and validation
3. Follow existing coding standards
4. Include unit tests where applicable
5. Add usage examples