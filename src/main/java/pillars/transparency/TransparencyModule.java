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
                result.addViolation("TRANSPARENCY: No explanation provided");
                result.escalate("Missing explanation requires review");
                return;
            }
        }
        
        // Algorithm 5: Step 2 - Assess explanation quality
        double quality = assessExplanationQuality(explanation);
        
        // Block if quality too low (< 0.5)
        if (quality < policy.minExplanationQuality) {
            result.addViolation(String.format(
                "TRANSPARENCY: Explanation quality %.2f below minimum %.2f",
                quality, policy.minExplanationQuality
            ));
            result.escalate("Low explanation quality requires review");
        }
        // Warn if quality borderline (0.5-0.7)
        else if (quality < policy.warningExplanationQuality) {
            result.addWarning(String.format(
                "TRANSPARENCY: Explanation quality %.2f is borderline (threshold: %.2f)",
                quality, policy.warningExplanationQuality
            ));
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
    
    /**
     * Assesses explanation quality based on length and content
     * Returns score from 0.0 to 1.0
     */
    private double assessExplanationQuality(String explanation) {
        if (explanation == null || explanation.isEmpty()) return 0.0;
        
        double quality = 0.0;
        
        // Length assessment
        if (explanation.length() > 30) quality += 0.3;
        if (explanation.length() > 60) quality += 0.2;
        
        // Content assessment - check for key terms
        String lower = explanation.toLowerCase();
        String[] keyTerms = {"credit", "score", "income", "risk", "threshold", "approved", "rejected", "based", "factors", "analysis"};
        int termsFound = 0;
        for (String term : keyTerms) {
            if (lower.contains(term)) termsFound++;
        }
        quality += Math.min(0.5, termsFound * 0.1);
        
        return Math.min(1.0, quality);
    }
}
