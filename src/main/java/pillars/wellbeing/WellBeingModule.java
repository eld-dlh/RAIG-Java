package pillars.wellbeing;

import core.*;

public class WellBeingModule {

    public void check(EthicsContext context, EthicsResult result) {
        if (context.decision.hasNegativeSocialImpact()) {
            result.addViolation("Well-being: Potential social harm detected");
        }
    }
}
