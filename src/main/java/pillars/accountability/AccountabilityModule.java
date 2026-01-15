package pillars.accountability;

import core.*;

public class AccountabilityModule {

    public void check(EthicsContext context, EthicsResult result) {
        if (context.decision.getResponsibleEntity() == null) {
            result.addViolation("Accountability: No responsible entity assigned");
        }
    }
}
