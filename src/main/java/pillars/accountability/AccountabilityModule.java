package pillars.accountability;

import core.EthicsContext;
import core.EthicsResult;
import config.EthicsPolicy;

/**
 * Implements Algorithm 1 from the paper: Accountability enforcement
 * Ensures every decision has a responsible entity assigned
 */
public class AccountabilityModule {
    
    public void check(EthicsContext context, EthicsResult result, EthicsPolicy policy) {
        // Algorithm 1: Step 1 - Verify responsible entity exists
        if (policy.requireResponsibleEntity) {
            String entity = context.decision.getResponsibleEntity();
            
            if (entity == null || entity.trim().isEmpty()) {
                result.addViolation("ACCOUNTABILITY: Decision must have a responsible entity assigned");
            }
        }
        
        // Algorithm 1: Step 2 - Log decision for audit trail
        // (Handled by DecisionHistory in ApprovalWorkflow)
    }
}
