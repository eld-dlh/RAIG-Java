package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the outcome of an ethics evaluation with three-state decision support
 * Implements APPROVE/BLOCK/ESCALATE as described in the paper
 */
public class EthicsResult {

    private final List<String> violations = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private EthicsDecision finalDecision = EthicsDecision.APPROVE;
    private String escalationReason;

    /* ---------------- Mutators ---------------- */

    public void addViolation(String violation) {
        violations.add(violation);
        if (finalDecision != EthicsDecision.ESCALATE) {
            finalDecision = EthicsDecision.BLOCK;
        }
    }
    
    public void addWarning(String warning) {
        warnings.add(warning);
    }
    
    public void escalate(String reason) {
        finalDecision = EthicsDecision.ESCALATE;
        escalationReason = reason;
    }

    /* ---------------- Accessors ---------------- */

    public List<String> getViolations() {
        return Collections.unmodifiableList(violations);
    }
    
    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    public boolean hasViolations() {
        return !violations.isEmpty();
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public EthicsDecision getFinalDecision() {
        return finalDecision;
    }
    
    public String getEscalationReason() {
        return escalationReason;
    }

    /**
     * Semantic alias for governance workflows.
     */
    public boolean isBlocked() {
        return finalDecision == EthicsDecision.BLOCK;
    }

    public boolean isApproved() {
        return finalDecision == EthicsDecision.APPROVE;
    }
    
    public boolean requiresEscalation() {
        return finalDecision == EthicsDecision.ESCALATE;
    }

    /* ---------------- Debug / Demo ---------------- */

    @Override
    public String toString() {
        return String.format("EthicsResult{decision=%s, violations=%d, warnings=%d}", 
                           finalDecision, violations.size(), warnings.size());
    }
}
