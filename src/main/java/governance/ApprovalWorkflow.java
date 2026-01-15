package governance;

import core.EthicsResult;
import core.EthicsDecision;
import data.DecisionHistory;

/**
 * Manages approval workflow for ethics decisions
 * Implements governance layer as described in paper Section IV.C
 */
public class ApprovalWorkflow {
    private DecisionHistory history;
    
    public ApprovalWorkflow() {
        this.history = new DecisionHistory();
    }
    
    /**
     * Approves a decision based on result and approver role
     */
    public boolean approve(EthicsResult result, Role approverRole) {
        // Log decision
        history.record(result);
        
        // Blocked decisions cannot be approved
        if (result.isBlocked()) {
            return false;
        }
        
        // Escalated decisions require specific roles
        if (result.requiresEscalation()) {
            return canApproveEscalation(approverRole);
        }
        
        // Approved decisions pass through
        return result.isApproved();
    }
    
    private boolean canApproveEscalation(Role role) {
        return role == Role.ETHICS_OFFICER || 
               role == Role.ADMIN || 
               role == Role.LEGAL_REVIEWER;
    }
    
    public DecisionHistory getHistory() {
        return history;
    }
}
