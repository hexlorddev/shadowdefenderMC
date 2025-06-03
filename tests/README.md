# Testing Framework

This directory contains comprehensive tests for ShadowDefenderMC.

## 🧪 Test Structure

### Unit Tests
- `ProtectionManagerTest.java` - Bot protection logic tests
- `VPNDetectionTest.java` - VPN detection algorithm tests  
- `ChallengeManagerTest.java` - Challenge mode functionality
- `ConfigManagerTest.java` - Configuration loading and validation
- `MessageUtilTest.java` - Message formatting and utilities

### Integration Tests
- `APIIntegrationTest.java` - External API testing
- `DatabaseIntegrationTest.java` - Database connectivity tests
- `BungeeCordIntegrationTest.java` - BungeeCord compatibility
- `EventSystemTest.java` - Custom event firing and handling

### Performance Tests
- `LoadTest.java` - High concurrent connection testing
- `MemoryLeakTest.java` - Long-running memory analysis
- `APIRateLimitTest.java` - API rate limiting behavior
- `CachePerformanceTest.java` - Cache efficiency testing

### Security Tests
- `SecurityTest.java` - Penetration testing scenarios
- `VPNBypassTest.java` - VPN detection bypass attempts
- `BotDetectionTest.java` - Advanced bot pattern testing
- `RateLimitBypassTest.java` - Rate limiting circumvention

## 🏃 Running Tests

### Quick Test Suite
```bash
mvn test
```

### Specific Test Categories
```bash
# Unit tests only
mvn test -Dtest="*Test"

# Integration tests
mvn test -Dtest="*IntegrationTest"

# Performance tests (may take longer)
mvn test -Dtest="*PerformanceTest" -Dtest.timeout=300
```

### Test with Coverage
```bash
mvn test jacoco:report
# View coverage at target/site/jacoco/index.html
```

## 📊 Test Data

### Mock Servers
The test framework includes mock servers for testing:
- Mock VPN API server
- Mock Minecraft server environment
- Mock BungeeCord proxy

### Test Configurations
- `test-config.yml` - Standard test configuration
- `performance-config.yml` - Performance testing config
- `security-config.yml` - Security testing scenarios

## 🔧 Test Configuration

### Database Testing
Tests can use embedded databases:
```yaml
test:
  database:
    type: "h2"
    memory: true
```

### API Testing
Mock API responses for consistent testing:
```yaml
test:
  mock_apis:
    vpn_detection: true
    response_delay: 100ms
```

## 📈 Continuous Integration

### GitHub Actions
Automated testing on:
- Push to main branch
- Pull request creation
- Release preparation

### Test Reports
- Unit test coverage reports
- Performance benchmarks
- Security scan results

## 🛠️ Writing Tests

### Test Conventions
1. One test class per main class
2. Test method names describe the scenario
3. Use meaningful assertions with messages
4. Mock external dependencies
5. Clean up resources in tearDown methods

### Example Test Structure
```java
@TestMethodOrder(OrderAnnotation.class)
class ProtectionManagerTest {
    
    @BeforeEach
    void setUp() {
        // Initialize test environment
    }
    
    @Test
    @Order(1)
    void testIPBanning() {
        // Test IP banning functionality
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test data
    }
}
```

## 🚀 Test Automation

### Pre-commit Hooks
```bash
#!/bin/bash
# .git/hooks/pre-commit
mvn test -q
if [ $? -ne 0 ]; then
    echo "Tests failed, commit aborted"
    exit 1
fi
```

### Performance Benchmarks
Automated performance testing to catch regressions:
- Connection handling throughput
- Memory usage patterns
- API response times
- Cache hit rates

## 📝 Test Documentation

Each test includes:
- Purpose description
- Test data requirements
- Expected outcomes
- Known limitations

See individual test files for detailed documentation.