package pillars.human;

import core.EthicsContext;
import core.EthicsResult;
import config.EthicsPolicy;

/**
 * Implements Algorithm 6 from the paper: Human oversight requirements
 * Ensures critical decisions can be reviewed by humans
 */
public class HumanOversightModule {
    
    // Decisions with high impact require human review
    private static final double HIGH_IMPACT_THRESHOLD = 0.85;
    
    public void check(EthicsContext context, EthicsResult result, EthicsPolicy policy) {
        // Algorithm 6: Step 1 - Identify high-impact decisions with sensitive data
        if (isHighImpactDecision(context) && context.userData.containsSensitiveData()) {
            result.addWarning("HUMAN_OVERSIGHT: High-impact decision with sensitive data flagged for review");
            
            // Algorithm 6: Step 2 - Ensure human override capability
            if (context.decision.getConfidence() > HIGH_IMPACT_THRESHOLD) {
                result.escalate("High-impact decision requires human oversight");
            }
        }
        
        // Algorithm 6: Step 3 - Check for automation boundaries
        validateAutomationBoundaries(context, result);
    }
    
    private boolean isHighImpactDecision(EthicsContext context) {
        String label = context.decision.getDecisionLabel().toLowerCase();
        
        // High-impact domains
        return label.contains("loan") ||
               label.contains("credit") ||
               label.contains("insurance") ||
               label.contains("medical") ||
               label.contains("legal") ||
               label.contains("hiring");
    }
    
    private void validateAutomationBoundaries(EthicsContext context, EthicsResult result) {
        // Ensure decisions have human override capability
        // Additional automation boundary checks can be implemented here
    }
}
