package pillars.transparency;

import core.EthicsContext;
import core.EthicsResult;
import config.EthicsPolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced transparency module with explanation quality assessment
 * Implements the paper's XAI integration requirements
 */
public class TransparencyModule {

    private static final Map<String, String> explanationMap = new HashMap<>();
    private double explanationQualityThreshold = 0.7;

    static {
        explanationMap.put("FAIRNESS",
                "The model showed unequal outcomes across demographic groups.");
        explanationMap.put("ROBUSTNESS",
                "The model confidence was below the accepted safety threshold.");
        explanationMap.put("PRIVACY",
                "Personal data was used without sufficient anonymization.");
        explanationMap.put("WELLBEING",
                "The decision may negatively impact societal or environmental well-being.");
    }

    public void check(EthicsContext context,
                      EthicsResult result,
                      EthicsPolicy policy) {

        if (!policy.requireExplanation) return;

        if (context.decision.getExplanation() == null) {
            String generatedExplanation = generateExplanation(result);
            context.decision.setExplanation(generatedExplanation);

            if (generatedExplanation == null) {
                result.addViolation("Transparency: Explanation unavailable");
            }
        }
        
        // Explanation quality assessment
        if (policy.requireExplanation && context.decision.getExplanation() != null) {
            double quality = assessExplanationQuality(context.decision.getExplanation());
            
            if (quality < 0.5) {
                result.addViolation(String.format(
                    "TRANSPARENCY: Explanation quality insufficient (%.2f)", quality
                ));
            } else if (quality < explanationQualityThreshold) {
                result.addWarning(String.format(
                    "TRANSPARENCY: Explanation quality marginal (%.2f)", quality
                ));
                result.escalate("Low explanation quality requires review");
            }
        }
    }

    private String generateExplanation(EthicsResult result) {
        if (result.getViolations().isEmpty()) {
            return "The AI decision complies with all ethical requirements. " +
                   "Key factors: standard risk assessment, regulatory compliance.";
        }

        StringBuilder explanation = new StringBuilder("Decision flagged due to: ");

        for (String violation : result.getViolations()) {
            explanation.append(
                    explanationMap.getOrDefault(
                            violation.split(":")[0].toUpperCase(),
                            violation
                    )
            ).append(" ");
        }
        return explanation.toString();
    }
    
    /**
     * Assess explanation quality based on length, detail, and feature coverage
     * Returns score from 0.0 to 1.0
     */
    private double assessExplanationQuality(String explanation) {
        if (explanation == null || explanation.isEmpty()) {
            return 0.0;
        }
        
        double score = 0.0;
        
        // Length check (explanations should be substantive)
        if (explanation.length() > 50) score += 0.3;
        if (explanation.length() > 100) score += 0.2;
        
        // Feature mention check
        String[] features = {"income", "credit", "employment", "risk", "history", "score", "factors"};
        int mentionedFeatures = 0;
        for (String feature : features) {
            if (explanation.toLowerCase().contains(feature)) {
                mentionedFeatures++;
            }
        }
        score += Math.min(0.5, mentionedFeatures * 0.1);
        
        return Math.min(1.0, score);
    }
    
    public void setQualityThreshold(double threshold) {
        this.explanationQualityThreshold = threshold;
    }
}

