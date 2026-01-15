# RAIG-Java: Quick Reference Guide

## What's New

The RAIG-Java framework has been enhanced to match the research paper with these major additions:

### 1. Three-State Decision System
```java
// Old (boolean)
boolean approved = result.isApproved();

// New (three-state)
EthicsDecision decision = result.getFinalDecision();
switch(decision) {
    case APPROVE -> System.out.println("Approved");
    case BLOCK -> System.out.println("Blocked");
    case ESCALATE -> System.out.println("Needs human review");
}
```

### 2. Maven Build System
```bash
# Old
javac *.java
java Main

# New
mvn clean compile
mvn test
mvn package
java -jar target/raig-java-1.0.0.jar
```

### 3. YAML Configuration
```yaml
# config/ethics-policy.yaml
ethics_engine:
  thresholds:
    max_bias: 0.3
    min_confidence: 0.5
  escalation:
    enabled: true
    queue_type: priority
```

### 4. Logging & Audit Trails
```java
// Automatic audit logging for all decisions
// Logs written to: logs/raig-audit.log
[2026-01-15 19:30:45.123] [INFO] EthicsEngine - 
  Decision: Loan Approved | Result: APPROVE | Latency: 12.3ms
```

### 5. Historical Fairness Analysis
```java
// FairnessModule now analyzes historical patterns
FairnessModule fairness = new FairnessModule();
DecisionHistory history = new DecisionHistory(1000);
fairness.setDecisionHistory(history);

// Automatically computes demographic parity
Map<String, Double> rates = history.computeApprovalRates("gender", 100);
double disparity = history.computeDemographicParity("gender", 100);
```

### 6. Escalation Queue
```java
// ApprovalWorkflow now handles escalations
ApprovalWorkflow workflow = new ApprovalWorkflow();
boolean approved = workflow.processDecision(context, result, "user@email.com");

// Check queue status
int pending = workflow.getPendingEscalations();
EscalationQueue queue = workflow.getEscalationQueue();
```

### 7. Enhanced Modules

#### Privacy Module - Data Minimization
```java
// Now validates feature necessity
// Blocks if unnecessary features are used
```

#### Robustness Module - Three-Tier Confidence
```java
// confidence < 0.5: BLOCK
// 0.5 <= confidence < 0.7: ESCALATE
// confidence >= 0.7: APPROVE (if no violations)
```

#### Transparency Module - Explanation Quality
```java
// Assesses explanation quality (0.0-1.0 score)
// Quality < 0.5: BLOCK
// 0.5 <= Quality < 0.7: ESCALATE
// Quality >= 0.7: APPROVE
```

## Updated File Structure

```
RAIG-Java/
├── pom.xml                     # NEW: Maven configuration
├── BUILD.md                    # NEW: Build & deployment guide
├── ENHANCEMENT_SUMMARY.md      # NEW: Enhancement details
├── .gitignore                  # NEW: Git ignore rules
├── config/
│   └── ethics-policy.yaml      # NEW: YAML configuration
├── src/
│   ├── main/
│   │   ├── java/               # All source code
│   │   │   ├── core/
│   │   │   │   ├── EthicsDecision.java     # NEW: Three-state enum
│   │   │   │   ├── EthicsEngine.java       # ENHANCED: Audit logging
│   │   │   │   └── EthicsResult.java       # ENHANCED: Warnings + escalation
│   │   │   ├── data/
│   │   │   │   └── DecisionHistory.java    # NEW: Historical analysis
│   │   │   ├── governance/
│   │   │   │   ├── ApprovalWorkflow.java   # ENHANCED: Three-state processing
│   │   │   │   └── EscalationQueue.java    # NEW: Human review queue
│   │   │   ├── pillars/
│   │   │   │   ├── fairness/
│   │   │   │   │   └── FairnessModule.java # ENHANCED: Algorithm 2
│   │   │   │   ├── privacy/
│   │   │   │   │   └── PrivacyGovernanceModule.java # ENHANCED: Algorithm 3
│   │   │   │   ├── robustness/
│   │   │   │   │   └── RobustnessSafetyModule.java # ENHANCED: Three-tier
│   │   │   │   └── transparency/
│   │   │   │       └── TransparencyModule.java # ENHANCED: Quality scoring
│   │   └── resources/
│   │       └── logback.xml     # NEW: Logging configuration
│   └── test/
│       └── java/               # NEW: JUnit 5 tests
│           ├── core/
│           │   └── EthicsEngineTest.java
│           └── pillars/
│               └── fairness/
│                   └── FairnessModuleTest.java
└── target/
    └── classes/                # Compiled bytecode
```

## Migration Guide

### For Existing Code

Your existing code will continue to work without changes:

```java
// Old code still works
EthicsResult result = engine.intercept(context);
if (result.isApproved()) {
    // proceed
} else {
    // block
}
```

### To Use New Features

```java
// Use three-state decision
EthicsResult result = engine.intercept(context);

if (result.isApproved()) {
    // Fully approved - proceed
} else if (result.requiresEscalation()) {
    // Send to human review
    workflow.processDecision(context, result, userEmail);
} else if (result.isBlocked()) {
    // Hard block - reject decision
}

// Access warnings
for (String warning : result.getWarnings()) {
    System.out.println("Warning: " + warning);
}

// Access escalation reason
if (result.requiresEscalation()) {
    System.out.println("Escalation reason: " + result.getEscalationReason());
}
```

## Key APIs

### EthicsResult
- `getFinalDecision()` → Returns APPROVE, BLOCK, or ESCALATE
- `getViolations()` → List of violations
- `getWarnings()` → List of warnings (NEW)
- `getEscalationReason()` → Why escalation needed (NEW)
- `isApproved()` → true if APPROVE
- `isBlocked()` → true if BLOCK
- `requiresEscalation()` → true if ESCALATE (NEW)

### EthicsEngine
- `intercept(context)` → Main evaluation method
- `getEvaluationCount()` → Total decisions processed (NEW)
- `getBlockedCount()` → Total blocked (NEW)
- `getEscalatedCount()` → Total escalated (NEW)
- `getBlockRate()` → Percentage blocked (NEW)
- `getEscalationRate()` → Percentage escalated (NEW)

### DecisionHistory (NEW)
- `record(decision, approved, demographics)` → Store decision
- `getRecent(count)` → Get recent decisions
- `computeApprovalRates(attribute, size)` → Group approval rates
- `computeDemographicParity(attribute, size)` → Statistical disparity

### EscalationQueue (NEW)
- `escalate(context, result, reason)` → Add to queue
- `poll()` → Get next for review
- `size()` → Pending count
- `getAll()` → All pending escalations

## Testing

### Run Tests
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=FairnessModuleTest

# With coverage
mvn jacoco:prepare-agent test jacoco:report
# Report: target/site/jacoco/index.html
```

### Test Scenarios Covered
1. ✅ Clean decision (all checks pass)
2. ✅ Privacy violation (missing consent)
3. ✅ High bias (fairness violation)
4. ✅ Low confidence (robustness violation)
5. ✅ Missing explanation (transparency violation)
6. ✅ Latency performance (<50ms)
7. ✅ Statistics tracking

## Configuration

### Change Thresholds
Edit `config/ethics-policy.yaml`:
```yaml
thresholds:
  max_bias: 0.3        # Fairness threshold
  min_confidence: 0.5  # Robustness threshold
  max_false_positive_rate: 0.05
```

### Change Logging
Edit `src/main/resources/logback.xml`:
```xml
<logger name="core" level="DEBUG"/>  <!-- Change to INFO for less verbosity -->
<logger name="pillars" level="DEBUG"/>
```

### Enable/Disable Modules
Edit `config/ethics-policy.yaml`:
```yaml
modules:
  - name: fairness
    enabled: true   # Set to false to disable
```

## Performance Benchmarks

Based on paper's experimental validation:
- **Latency**: <15ms per evaluation (target)
- **Throughput**: ~1000 evaluations/second
- **Memory**: ~2MB for 1000-entry history
- **Accuracy**: 95% violation detection rate (paper claim)

## Production Deployment

See [BUILD.md](BUILD.md) for:
- Docker deployment
- Kubernetes deployment
- Performance tuning
- Monitoring setup
- Security hardening

## Support & Documentation

- **Build Guide**: BUILD.md
- **Enhancement Details**: ENHANCEMENT_SUMMARY.md
- **Research Paper**: Information/researchpaper.txt
- **Project README**: README.md

## Version History

- **v1.0.0** (2026-01-15)
  - Initial enhanced release
  - Maven build system
  - Three-state decision system
  - Historical fairness analysis
  - Escalation queue
  - Comprehensive testing
  - Production-ready logging
