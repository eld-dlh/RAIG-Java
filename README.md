# RAIG-Java
RAIG-Java is a modular, Java implementation of the Responsible AI Governance framework proposed by Papagiannidis et al. (2025). This repository moves beyond abstract ethical theory by operationalizing AI principles into a "Digital Guardrail" system designed for the entire AI lifecycle.
---

## Overview

The framework focuses on decision‑level governance across seven responsible AI pillars:

- Accountability  
- Fairness and non‑discrimination  
- Human agency and oversight  
- Privacy and data governance  
- Technical robustness and safety  
- Transparency and explainability  
- Societal and environmental well‑being

These principles are implemented as pluggable Java modules that can be applied to any AI decision before deployment.

---

## Architecture

**Core objects**

- `AIDecision` – Represents a single model decision (label, confidence, bias score, explanation, responsible entity, and hooks for model/dataset references).  
- `UserData` – Represents the affected user, including consent and sensitive‑data flags.  
- `EthicsContext` – Combines `AIDecision` and `UserData` into a single evaluation context.  
- `EthicsResult` – Collects ethics violations and exposes `hasViolations()` / `isBlocked()` for governance logic.

**Ethics engine**

- `EthicsEngine` – Runs the context through all pillars and returns an `EthicsResult`.  
- Pillar modules (each implements a `check(...)` method), for example:
  - `PrivacyGovernanceModule`  
  - `WellBeingModule`  
  - `FairnessModule`  
  - `RobustnessSafetyModule`  
  - `TransparencyModule`  
  - `HumanOversightModule`  
  - `AccountabilityModule`

**Governance layer**

- `EthicsPolicy` – Central configuration with thresholds:
  - `maxBias = 0.3` (bias scores above this trigger fairness violations)
  - `minConfidence = 0.5` (confidence below this triggers robustness violations)
  - `requireExplanation = true` (transparency requirement)
- `PolicyManager` – Centralized policy access for all modules.  
- `Role`, `RoleManager` – Governance roles (e.g., ETHICS_OFFICER).  
- `ApprovalWorkflow` – Uses role + `EthicsResult` to approve or block deployment.  
- `FeedbackService`, `StakeholderFeedback` – Allows critical stakeholder input to block decisions.

**Integration hooks (optional)**

- `SparkBiasAnalyzer` – Apache Spark integration for bias analysis.  
- `TrustyAIAdapter` – TrustyAI integration for bias computation (returns 0.42 in demo).  
- `DL4JModelWrapper` – DeepLearning4J model wrapper example.

---

## Web Interface

The `Main` class provides a comprehensive web interface for the RAIG framework through an HTTP server on port 8080. It features:

### REST API Endpoints

- **GET `/api/pillars`** – Returns the 7 ethics pillars with descriptions, icons, colors, and key checks
- **GET `/api/scenarios`** – Returns 4 pre-configured demo scenarios for testing
- **POST `/api/evaluate`** – Evaluates AI decisions through the ethics framework
- **GET `/`** – Serves the interactive web UI with dark theme

### Demo Scenarios

Four built-in scenarios demonstrate the governance flow:

1. **Loan Approval** – Clean decision with low bias, passes all checks
2. **Medical Diagnosis** – High bias scenario (0.85) triggers fairness violations  
3. **Credit Scoring** – Privacy violation (no consent + sensitive data) blocks approval
4. **Job Screening** – Low confidence (0.45) triggers robustness checks

### Evaluation Flow

Each evaluation:

1. Parses JSON request with `AIDecision` and `UserData` fields
2. Builds an `EthicsContext` combining decision and user data
3. Passes through `EthicsEngine.intercept(...)` for pillar checks
4. Returns JSON with `approved` status, violations list, and decision details

---

## Getting Started

### Requirements

- Java 17+  
- No additional dependencies (uses built-in `com.sun.net.httpserver`)

### Build & Run

```bash
git clone https://github.com/<your-username>/RAIG-Java.git
cd RAIG-Java

# Compile
javac Main.java

# Run web server
java Main
```

The server will start on `http://localhost:8080`. Open your browser to access the interactive web interface with:
- Real-time ethics evaluation
- Interactive decision testing form
- Pre-configured demo scenarios
- Detailed violation reporting
- Modern dark theme UI

---
**Modular Ethics Pillars** – 7 independent modules with clear separation of concerns
- **RESTful API** – Three endpoints for pillars, scenarios, and evaluation
- **Interactive Web UI** – Modern dark theme interface with real-time feedback
- **Decision-Level Guardrails** – Wrap any AI model output with ethics checks
- **Configurable Thresholds** – Adjust bias, confidence, and transparency requirements
- **Bias Detection** – Automatic computation via TrustyAI or manual specification
- **Privacy Controls** – Consent and sensitive data validation
- **Role-Based Governance** – Approval workflow with stakeholder feedback
- **Comprehensive Testing** – 7 test scenarios covering all violation types
- **Zero External Dependencies** – Pure Java implementation using built-in HTTP server

---

## Testing

The framework includes comprehensive test coverage:

1. **Clean Decision** – All checks pass (bias=0.2, confidence=0.85)
2. **Privacy Violation** – No consent with sensitive data
3. **High Bias** – Bias score 0.8 exceeds 0.3 threshold
4. **Low Confidence** – Confidence 0.3 below 0.5 threshold
5. **Negative Social Impact** – Well-being pillar violation
6. **Multiple Violations** – Combined privacy, bias, and robustness failures
7. **TrustyAI Auto-Computation** – Automatic bias detection (0.42 > 0.3)

All tests validate the complete ethics evaluation pipeline from input to approval/rejection 
- Role‑based approval workflow for governance.  
- Stakeholder feedback loop that can block problematic decisions.  
- Integration points for bias analysis and explainability tools.
```
