package governance;

import core.EthicsResult;
import core.EthicsContext;
import core.EthicsDecision;

/**
 * Enhanced approval workflow with three-state decision support and escalation queue
 * Implements the human oversight workflow from the paper
 */
public class ApprovalWorkflow {

    private RoleManager roleManager = new RoleManager();
    private EscalationQueue escalationQueue = new EscalationQueue(1000);

    /**
     * Process ethics result with three-state logic: APPROVE, BLOCK, or ESCALATE
     * Returns final approval decision after human review if needed
     */
    public boolean processDecision(EthicsContext context, EthicsResult result, String userEmail) {
        
        // Case 1: Decision is approved - no action needed
        if (result.isApproved()) {
            return true;
        }
        
        // Case 2: Decision requires escalation - add to review queue
        if (result.requiresEscalation()) {
            EscalationQueue.EscalatedDecision escalated = escalationQueue.escalate(
                context, 
                result, 
                result.getEscalationReason()
            );
            
            // In production, this would notify reviewers and wait for response
            // For now, auto-escalate to ethics officer
            return escalated != null;
        }
        
        // Case 3: Decision is blocked - check for authorized override
        if (result.isBlocked()) {
            return reviewAndOverride(result, userEmail);
        }
        
        return false;
    }

    /**
     * If an ethics violation is found, a privileged user (ETHICS_OFFICER)
     * can review and override the decision if justified.
     */
    public boolean reviewAndOverride(EthicsResult result, String userEmail) {
        if (!result.isBlocked()) {
            return true; // no override needed
        }

        boolean hasPrivilege = roleManager.hasRole(userEmail, Role.ETHICS_OFFICER);
        if (hasPrivilege) {
            // Placeholder: in practice, user would provide justification
            return true; // approved by override
        }

        return false; // stay blocked
    }
    
    // Legacy compatibility method
    public boolean approve(EthicsResult result, Role role) {
        if (!result.isApproved()) return false;
        return RoleManager.canApprove(role);
    }
    
    public EscalationQueue getEscalationQueue() {
        return escalationQueue;
    }
    
    public int getPendingEscalations() {
        return escalationQueue.size();
    }
}
