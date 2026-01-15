package pillars.transparency;

import core.EthicsContext;
import core.EthicsResult;
import config.EthicsPolicy;

/**
 * Implements Algorithm 5 from the paper: Explainability enforcement
 * Ensures all decisions have explanations
 */
public class TransparencyModule {
    
    public void check(EthicsContext context, EthicsResult result, EthicsPolicy policy) {
        String explanation = context.decision.getExplanation();
        
        // Algorithm 5: Step 1 - Check if explanation exists
        if (policy.requireExplanation) {
            if (explanation == null || explanation.trim().isEmpty()) {
                // Algorithm 5: Step 2 - Generate explanation if missing
                String generated = generateExplanation(context);
                context.decision.setExplanation(generated);
                
                result.addWarning("TRANSPARENCY: Explanation was auto-generated");
            }
        }
        
        // Algorithm 5: Step 3 - Validate explanation quality
        if (explanation != null && explanation.length() < 20) {
            result.addWarning("TRANSPARENCY: Explanation is too brief");
        }
    }
    
    /**
     * Generates a basic explanation when none is provided
     */
    private String generateExplanation(EthicsContext context) {
        return String.format(
            "Decision '%s' made with confidence %.2f by %s",
            context.decision.getDecisionLabel(),
            context.decision.getConfidence(),
            context.decision.getResponsibleEntity() != null ? 
                context.decision.getResponsibleEntity() : "unknown system"
        );
    }
}
