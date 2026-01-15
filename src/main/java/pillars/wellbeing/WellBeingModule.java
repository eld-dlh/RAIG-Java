package pillars.wellbeing;

import core.EthicsContext;
import core.EthicsResult;
import config.EthicsPolicy;

/**
 * Implements Algorithm 7 from the paper: Societal and environmental well-being
 * Assesses broader impact of AI decisions
 */
public class WellBeingModule {
    
    public void check(EthicsContext context, EthicsResult result, EthicsPolicy policy) {
        // Algorithm 7: Step 1 - Assess social impact
        if (context.decision.hasNegativeSocialImpact()) {
            result.addViolation("WELL_BEING: Decision flagged for negative social impact");
        }
        
        // Algorithm 7: Step 2 - Check for environmental concerns
        assessEnvironmentalImpact(context, result);
        
        // Algorithm 7: Step 3 - Evaluate societal consequences
        assessSocietalConsequences(context, result);
    }
    
    private void assessEnvironmentalImpact(EthicsContext context, EthicsResult result) {
        // In production, this would check carbon footprint, resource usage, etc.
        // For now, we flag high-confidence decisions that might have broader impact
        if (context.decision.getConfidence() > 0.95) {
            result.addWarning("WELL_BEING: High-confidence decision should consider environmental impact");
        }
    }
    
    private void assessSocietalConsequences(EthicsContext context, EthicsResult result) {
        String label = context.decision.getDecisionLabel().toLowerCase();
        
        // Flag decisions that might have societal consequences
        if (label.contains("reject") || label.contains("deny") || label.contains("decline")) {
            result.addWarning("WELL_BEING: Negative decision - consider societal impact");
        }
    }
}
