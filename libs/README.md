# External Libraries and Dependencies

This directory contains external libraries and documentation for third-party dependencies used by ShadowDefenderMC.

## 📚 Core Dependencies

### Runtime Dependencies
- **Spigot API** (1.20.1-R0.1-SNAPSHOT) - Core Minecraft server API
- **BungeeCord API** (1.19-R0.1-SNAPSHOT) - Proxy server API  
- **Google Gson** (2.10.1) - JSON parsing and serialization
- **SLF4J** (1.7.32) - Logging facade
- **Guava** (31.1-jre) - Core utilities and collections

### Development Dependencies
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for tests
- **Maven Shade Plugin** - JAR packaging
- **Maven Compiler Plugin** - Java compilation

## 🔧 Optional Dependencies

### Performance Libraries
- **Caffeine** - High-performance caching library
- **Chronicle Map** - Off-heap key-value store
- **Netty** - High-performance networking
- **HikariCP** - JDBC connection pooling

### Monitoring Libraries
- **Micrometer** - Application metrics facade
- **Prometheus Client** - Metrics collection
- **JMX** - Java Management Extensions
- **ASM** - Bytecode manipulation

### Utility Libraries
- **Apache Commons** - Utility functions
- **Jackson** - Alternative JSON processing
- **OkHttp** - HTTP client library
- **Jedis** - Redis Java client

## 📦 Dependency Management

### Maven Configuration
```xml
<dependencies>
    <!-- Core Dependencies -->
    <dependency>
        <groupId>org.spigotmc</groupId>
        <artifactId>spigot-api</artifactId>
        <version>1.20.1-R0.1-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- JSON Processing -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.9.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Gradle Configuration
```gradle
dependencies {
    // Core Dependencies
    compileOnly 'org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT'
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // Testing
    testImplementation 'org.junit.jupiter:junit-jupiter:5.9.2'
    testImplementation 'org.mockito:mockito-core:5.1.1'
}
```

## 🔍 Version Compatibility

### Minecraft Version Support
- **1.8-1.12**: Basic functionality, limited features
- **1.13-1.16**: Full feature support
- **1.17-1.20**: Recommended, all features
- **1.21+**: Future compatibility maintained

### Java Version Requirements
- **Java 8**: Minimum requirement, basic features
- **Java 11**: Recommended, full performance
- **Java 17**: Latest LTS, optimal performance
- **Java 21**: Future-ready, cutting-edge features

### Server Software Compatibility
```yaml
Spigot:
  versions: "1.8-1.20+"
  features: "Full support"
  
Paper:
  versions: "1.8-1.20+"
  features: "Enhanced performance"
  
BungeeCord:
  versions: "1.8-1.20+"
  features: "Network-wide protection"
  
Velocity:
  versions: "3.0+"
  features: "Modern proxy support"
```

## 📊 License Information

### Open Source Libraries
All dependencies are compatible with the MIT License:

- **Apache License 2.0**: Gson, Commons libraries
- **MIT License**: SLF4J, JUnit
- **GPL v3**: Spigot API (plugin compatible)
- **BSD License**: Guava, Netty

### Commercial Use
All dependencies allow commercial use and redistribution.

## 🛠️ Building with Dependencies

### Local Development
```bash
# Install dependencies locally
mvn dependency:resolve

# Copy dependencies to lib folder
mvn dependency:copy-dependencies -DoutputDirectory=lib

# Build with all dependencies
mvn clean package shade:shade
```

### Production Build
```bash
# Optimized build for production
mvn clean package -Doptimize=true -Dminify=true

# Create distribution package
mvn assembly:assembly -Ddescriptor=src/assembly/distribution.xml
```

## ⚡ Performance Optimization

### Dependency Optimization
- **Shade only required classes** to reduce JAR size
- **Use optional dependencies** where possible
- **Lazy load** heavy libraries
- **Cache** frequently used instances

### Memory Management
```java
// Example: Efficient dependency usage
public class OptimizedManager {
    private static final Gson GSON = new GsonBuilder()
        .disableHtmlEscaping()
        .create();
    
    // Reuse instances instead of creating new ones
    private final LoadingCache<String, Object> cache = 
        Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(this::loadData);
}
```

## 🔄 Dependency Updates

### Update Strategy
1. **Monitor security advisories** for all dependencies
2. **Test updates** in development environment
3. **Gradual rollout** to production servers
4. **Rollback plan** for problematic updates

### Automated Checks
```yaml
# GitHub Actions dependency check
- name: Check Dependencies
  run: mvn dependency:analyze dependency:tree
  
- name: Security Scan
  run: mvn org.owasp:dependency-check-maven:check
```

## 📖 Documentation Links

### API Documentation
- **Spigot API**: [hub.spigotmc.org/javadocs](https://hub.spigotmc.org/javadocs/)
- **BungeeCord API**: [ci.md-5.net/javadoc](https://ci.md-5.net/javadoc/)
- **Gson**: [javadoc.io/gson](https://javadoc.io/doc/com.google.code.gson/gson/)

### Best Practices
- **Maven**: [maven.apache.org/guides](https://maven.apache.org/guides/)
- **Gradle**: [docs.gradle.org](https://docs.gradle.org/)
- **Plugin Development**: [spigotmc.org/wiki](https://www.spigotmc.org/wiki/)

## 🆘 Dependency Issues

### Common Problems
1. **ClassNotFoundException**: Check classpath and shading
2. **Version conflicts**: Use dependency management
3. **Security vulnerabilities**: Regular updates required
4. **Performance issues**: Profile and optimize

### Troubleshooting
```bash
# Check dependency tree
mvn dependency:tree

# Analyze unused dependencies
mvn dependency:analyze

# Check for updates
mvn versions:display-dependency-updates
```

## 🔒 Security Considerations

### Dependency Security
- **Regular updates** to patch vulnerabilities
- **OWASP dependency check** in CI/CD pipeline
- **Minimal dependencies** to reduce attack surface
- **Trusted sources** only for dependencies

### Vulnerability Monitoring
```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>8.4.0</version>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS>
    </configuration>
</plugin>
```