package core;

import pillars.accountability.*;
import pillars.fairness.*;
import pillars.human.*;
import pillars.privacy.*;
import pillars.robustness.*;
import pillars.transparency.*;
import pillars.wellbeing.*;

public class EthicsEngine {

    private AccountabilityModule accountability = new AccountabilityModule();
    private FairnessModule fairness = new FairnessModule();
    private HumanOversightModule human = new HumanOversightModule();
    private PrivacyGovernanceModule privacy = new PrivacyGovernanceModule();
    private RobustnessSafetyModule robustness = new RobustnessSafetyModule();
    private TransparencyModule transparency = new TransparencyModule();
    private WellBeingModule wellbeing = new WellBeingModule();

    public EthicsResult evaluate(EthicsContext context) {
        EthicsResult result = new EthicsResult();

        accountability.check(context, result);
        fairness.check(context, result);
        human.check(context, result);
        privacy.check(context, result);
        robustness.check(context, result);
        transparency.check(context, result);
        wellbeing.check(context, result);

        return result;
    }
}
