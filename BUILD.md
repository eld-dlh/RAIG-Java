# RAIG-Java Build & Deployment Guide

## Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Apache Maven 3.8+**
- **Git** for version control

## Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/<your-username>/RAIG-Java.git
cd RAIG-Java
```

### 2. Build with Maven
```bash
# Clean build
mvn clean compile

# Run tests
mvn test

# Package JAR
mvn package
```

### 3. Run Web Server
```bash
# Using Maven
mvn exec:java -Dexec.mainClass="Main"

# Or using compiled JAR
java -jar target/raig-java-1.0.0.jar

# Or traditional method
javac Main.java
java Main
```

The server starts on `http://localhost:8080`

## Configuration

### Ethics Policy Configuration
Edit `config/ethics-policy.yaml` to customize:
- Bias thresholds
- Confidence requirements
- Module enable/disable
- Escalation settings

### Logging Configuration
Edit `src/main/resources/logback.xml` to configure:
- Log levels
- Output destinations
- Audit trail retention

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=FairnessModuleTest
mvn test -Dtest=EthicsEngineTest
```

### Generate Test Coverage Report
```bash
mvn jacoco:prepare-agent test jacoco:report
# Report available at: target/site/jacoco/index.html
```

## Deployment Options

### 1. Embedded Library
Add as Maven dependency to your project:
```xml
<dependency>
    <groupId>com.raig</groupId>
    <artifactId>raig-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. REST Microservice
```bash
# Run standalone HTTP server
java -jar target/raig-java-1.0.0.jar --server-mode
```

### 3. Docker Container
```bash
# Build image
docker build -t raig-java:latest .

# Run container
docker run -p 8080:8080 raig-java:latest
```

### 4. Kubernetes Deployment
```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## Performance Tuning

### JVM Options
```bash
java -Xmx2G -Xms512M \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar target/raig-java-1.0.0.jar
```

### Concurrent Request Handling
Adjust thread pool in Main.java:
```java
server.setExecutor(Executors.newFixedThreadPool(50));
```

## Monitoring

### Audit Logs
- Location: `logs/raig-audit.log`
- Rotation: Daily
- Retention: 30 days

### Metrics Endpoints
- `/metrics/evaluations` - Total decisions evaluated
- `/metrics/violations` - Violation statistics
- `/metrics/latency` - Performance metrics

## Troubleshooting

### Port Already in Use
```bash
# Change port in Main.java or use environment variable
export RAIG_PORT=8081
java -jar target/raig-java-1.0.0.jar
```

### Out of Memory
```bash
# Increase heap size
java -Xmx4G -jar target/raig-java-1.0.0.jar
```

### Logging Issues
Check `src/main/resources/logback.xml` and ensure `logs/` directory exists

## Production Checklist

- [ ] Configure production `ethics-policy.yaml`
- [ ] Set appropriate log levels
- [ ] Enable audit trail persistence
- [ ] Configure escalation queue size
- [ ] Set up monitoring/alerting
- [ ] Review security settings
- [ ] Load test under expected traffic
- [ ] Document custom modules
- [ ] Train ethics review team
- [ ] Establish incident response process

## License

Apache 2.0 - See LICENSE file for details

## Support

For issues and questions:
- GitHub Issues: https://github.com/<your-username>/RAIG-Java/issues
- Email: raig-support@example.com
