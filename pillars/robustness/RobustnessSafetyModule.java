package pillars.robustness;

import core.*;
import config.EthicsPolicy;

public class RobustnessSafetyModule {

    public void check(EthicsContext context, EthicsResult result, EthicsPolicy policy) {
        if (context.decision.getConfidence() < policy.minConfidence) {
            result.addViolation("Robustness: Low confidence prediction");
        }
    }
}
