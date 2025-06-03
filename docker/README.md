# Docker Support

Containerized deployment options for ShadowDefenderMC testing and development.

## Container Images

### Development Environment
Complete development environment with:
- OpenJDK 17
- Maven 3.8+
- Git
- Pre-configured IDE settings

### Testing Environment
Isolated testing environment with:
- Multiple Minecraft server versions
- Network simulation tools
- Attack simulation frameworks
- Monitoring dashboards

### Production Environment
Production-ready containers:
- Minimal JRE runtime
- Security hardening
- Resource optimization
- Health checks

## Docker Compose Configurations

### Single Server Testing
```yaml
version: '3.8'
services:
  minecraft-server:
    image: itzg/minecraft-server:java17
    environment:
      EULA: "TRUE"
      TYPE: "PAPER"
      VERSION: "1.20.1"
    volumes:
      - ./ShadowDefenderMC.jar:/plugins/ShadowDefenderMC.jar
    ports:
      - "25565:25565"
```

### Multi-Server Network
```yaml
version: '3.8'
services:
  bungee-proxy:
    image: itzg/bungeecord
    volumes:
      - ./ShadowDefenderMC.jar:/plugins/ShadowDefenderMC.jar
    ports:
      - "25577:25577"
  
  lobby-server:
    image: itzg/minecraft-server:java17
    environment:
      SERVER_NAME: "lobby"
  
  survival-server:
    image: itzg/minecraft-server:java17
    environment:
      SERVER_NAME: "survival"
```

### Development Stack
```yaml
version: '3.8'
services:
  minecraft-dev:
    build: ./docker/dev
    volumes:
      - ./:/workspace
      - maven-cache:/root/.m2
    
  database:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: shadowdefender
  
  monitoring:
    image: grafana/grafana
    ports:
      - "3000:3000"
```

## Build Instructions

### Build Development Image
```bash
docker build -f docker/Dockerfile.dev -t shadowdefender:dev .
```

### Build Production Image
```bash
docker build -f docker/Dockerfile.prod -t shadowdefender:latest .
```

### Multi-arch Build
```bash
docker buildx build --platform linux/amd64,linux/arm64 -t shadowdefender:multiarch .
```

## Container Registry

### GitHub Container Registry
```bash
docker tag shadowdefender:latest ghcr.io/hexlorddev/shadowdefender:latest
docker push ghcr.io/hexlorddev/shadowdefender:latest
```

### Docker Hub
```bash
docker tag shadowdefender:latest hexlorddev/shadowdefender:latest
docker push hexlorddev/shadowdefender:latest
```

## Kubernetes Deployment

### Basic Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: minecraft-shadowdefender
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: minecraft
        image: itzg/minecraft-server:java17
        volumeMounts:
        - name: plugins
          mountPath: /plugins
```

### Production Cluster
- Load balancer configuration
- Persistent volume storage
- Resource limits and requests
- Health check probes
- Auto-scaling policies

## Security Considerations

### Container Security
- Non-root user execution
- Read-only file systems
- Security context constraints
- Network policies
- Resource limitations

### Secrets Management
- Kubernetes secrets
- Docker secrets
- Environment variable encryption
- External secret management

---
**ShadowDefenderMC** - Created by HexLordDev