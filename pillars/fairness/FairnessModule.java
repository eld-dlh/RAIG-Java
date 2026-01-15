package pillars.fairness;

import core.EthicsContext;
import core.EthicsResult;
import config.EthicsPolicy;
import integration.trustyai.TrustyAIAdapter;

public class FairnessModule {

    private TrustyAIAdapter trustyAI = new TrustyAIAdapter();

    public void check(EthicsContext context,
                      EthicsResult result,
                      EthicsPolicy policy) {

        try {
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
                result.addViolation("FAIRNESS: Bias threshold exceeded");
            }

        } catch (Exception e) {
            // Integration failure becomes an ethics issue, not a crash
            result.addViolation(
                    "TRANSPARENCY: Fairness analysis unavailable (" +
                    e.getClass().getSimpleName() + ")"
            );
        }
    }
}
