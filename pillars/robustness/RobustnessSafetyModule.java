package pillars.robustness;

import core.*;

public class RobustnessSafetyModule {

    public void check(EthicsContext context, EthicsResult result) {
        if (context.decision.getConfidence() < 0.5) {
            result.addViolation("Robustness: Low confidence prediction");
        }
    }
}
