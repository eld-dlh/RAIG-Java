package pillars.fairness;

import core.EthicsContext;
import core.EthicsResult;
import config.EthicsPolicy;
import integration.trustyai.TrustyAIAdapter;
import data.DecisionHistory;
import java.util.*;

/**
 * Implements Algorithm 2 from the paper: Group-based fairness analysis
 * Computes demographic parity and detects disparate impact
 */
public class FairnessModule {

    private TrustyAIAdapter trustyAI = new TrustyAIAdapter();
    private DecisionHistory history;
    private double warningThreshold = 0.03;
    private int minSampleSize = 100;
    
    public FairnessModule() {
        this.history = new DecisionHistory(1000);
    }
    
    public void setDecisionHistory(DecisionHistory history) {
        this.history = history;
    }

    public void check(EthicsContext context,
                      EthicsResult result,
                      EthicsPolicy policy) {

        try {
            // Individual bias score check
            double biasScore = context.decision.getBiasScore();
            
            // Only compute bias if not already provided
            if (biasScore == 0.0) {
                biasScore = trustyAI.computeBiasScore(
                        context.decision.getDataset(),
                        context.decision.getModel()
                );
                context.decision.setBiasScore(biasScore);
            }

            if (biasScore > policy.maxBias) {
                result.addViolation("FAIRNESS: Bias threshold exceeded (score=" + 
                                  String.format("%.3f", biasScore) + ")");
            } else if (biasScore > warningThreshold) {
                result.addWarning("FAIRNESS: Bias approaching threshold (score=" + 
                                String.format("%.3f", biasScore) + ")");
            }
            
            // Group-based fairness analysis (Algorithm 2 from paper)
            if (history != null && history.size() >= minSampleSize) {
                performHistoricalAnalysis(result, policy);
            }

        } catch (Exception e) {
            // Integration failure becomes an ethics issue, not a crash
            result.addViolation(
                    "TRANSPARENCY: Fairness analysis unavailable (" +
                    e.getClass().getSimpleName() + ")"
            );
        }
    }
    
    /**
     * Algorithm 2: Historical fairness analysis
     * Computes approval rate disparity across demographic groups
     */
    private void performHistoricalAnalysis(EthicsResult result, EthicsPolicy policy) {
        String[] protectedAttributes = {"gender", "race", "age"};
        
        for (String attribute : protectedAttributes) {
            Map<String, Double> groupMetrics = history.computeApprovalRates(attribute, minSampleSize);
            
            if (groupMetrics.size() < 2) {
                continue; // Need at least 2 groups to compare
            }
            
            double maxRate = Collections.max(groupMetrics.values());
            double minRate = Collections.min(groupMetrics.values());
            double disparity = maxRate - minRate;
            
            if (disparity > policy.maxBias) {
                result.addViolation(String.format(
                    "FAIRNESS: Demographic disparity detected for %s (disparity=%.3f, threshold=%.3f)",
                    attribute, disparity, policy.maxBias
                ));
            } else if (disparity > warningThreshold) {
                result.addWarning(String.format(
                    "FAIRNESS: Potential %s disparity (%.3f)", attribute, disparity
                ));
                result.escalate("Borderline fairness metrics require human review");
            }
        }
    }
}
