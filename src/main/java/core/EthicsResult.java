package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of an ethics evaluation
 * Contains violations, warnings, and final decision state
 */
public class EthicsResult {
    private EthicsDecision finalDecision;
    private List<String> violations;
    private List<String> warnings;
    private String escalationReason;
    
    public EthicsResult() {
        this.finalDecision = EthicsDecision.APPROVE;
        this.violations = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    public void addViolation(String violation) {
        violations.add(violation);
        this.finalDecision = EthicsDecision.BLOCK;
    }
    
    public void addWarning(String warning) {
        warnings.add(warning);
    }
    
    public void escalate(String reason) {
        this.escalationReason = reason;
        if (this.finalDecision != EthicsDecision.BLOCK) {
            this.finalDecision = EthicsDecision.ESCALATE;
        }
    }
    
    public boolean isApproved() {
        return finalDecision == EthicsDecision.APPROVE;
    }
    
    public boolean isBlocked() {
        return finalDecision == EthicsDecision.BLOCK;
    }
    
    public boolean requiresEscalation() {
        return finalDecision == EthicsDecision.ESCALATE;
    }
    
    public boolean hasViolations() {
        return !violations.isEmpty();
    }
    
    public EthicsDecision getFinalDecision() {
        return finalDecision;
    }
    
    public List<String> getViolations() {
        return violations;
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
    
    public String getEscalationReason() {
        return escalationReason;
    }
}
