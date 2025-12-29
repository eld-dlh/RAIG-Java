package pillars.human;

import core.*;

public class HumanOversightModule {

    public void check(EthicsContext context, EthicsResult result) {
        if (!context.decision.isHumanOverrideAvailable()) {
            result.addViolation("Human Oversight: No override mechanism");
        }
    }
}
