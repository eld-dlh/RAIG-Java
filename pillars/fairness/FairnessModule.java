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

        double biasScore = trustyAI.computeBiasScore(
                context.decision.getDataset(),
                context.decision.getModel()
        );

        context.decision.setBiasScore(biasScore);

        if (biasScore > policy.maxBias) {
            result.addViolation("FAIRNESS: Bias threshold exceeded");
        }
    }
}
