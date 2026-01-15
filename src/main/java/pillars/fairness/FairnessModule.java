package pillars.fairness;

import core.EthicsContext;
import core.EthicsResult;
import config.EthicsPolicy;
import java.util.Random;

/**
 * Implements Algorithm 2 from the paper: Bias detection and mitigation
 * Checks for discriminatory bias in AI decisions
 */
public class FairnessModule {
    private Random random = new Random(42); // Seeded for reproducibility
    
    public void check(EthicsContext context, EthicsResult result, EthicsPolicy policy) {
        double biasScore = context.decision.getBiasScore();
        
        // Algorithm 2: Step 1 - Compute bias if not provided
        if (biasScore < 0) {
            biasScore = computeBiasScore(context);
            context.decision.setBiasScore(biasScore);
        }
        
        // Algorithm 2: Step 2 - Check against threshold
        if (biasScore > policy.maxBias) {
            result.addViolation(String.format(
                "FAIRNESS: Bias score %.2f exceeds threshold %.2f",
                biasScore, policy.maxBias
            ));
        }
        
        // Algorithm 2: Step 3 - Escalate at exactly the threshold
        if (biasScore == policy.maxBias) {
            result.escalate("Bias score at threshold - requires review");
        }
        
        // Algorithm 2: Step 4 - Warn on borderline cases (50% of threshold onwards)
        if (biasScore >= 0.15 && biasScore < policy.maxBias) {
            result.addWarning(String.format(
                "FAIRNESS: Bias score %.2f is near threshold %.2f",
                biasScore, policy.maxBias
            ));
        }
    }
    
    /**
     * Simulates TrustyAI bias computation
     * In production, this would call actual TrustyAI service
     */
    private double computeBiasScore(EthicsContext context) {
        // Simulate TrustyAI returning bias scores
        // Returns value between 0.1 and 0.9
        return 0.1 + (random.nextDouble() * 0.8);
    }
}
