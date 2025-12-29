package core;

import config.PolicyManager;
import communication.FeedbackService;
import pillars.accountability.*;
import pillars.fairness.*;
import pillars.human.*;
import pillars.privacy.*;
import pillars.robustness.*;
import pillars.transparency.*;
import pillars.wellbeing.*;

public class EthicsEngine {

    private PrivacyGovernanceModule privacy = new PrivacyGovernanceModule();
    private WellBeingModule wellbeing = new WellBeingModule();
    private FairnessModule fairness = new FairnessModule();
    private RobustnessSafetyModule robustness = new RobustnessSafetyModule();
    private TransparencyModule transparency = new TransparencyModule();
    private HumanOversightModule human = new HumanOversightModule();
    private AccountabilityModule accountability = new AccountabilityModule();

    public EthicsResult intercept(EthicsContext context) {

        EthicsResult result = new EthicsResult();

        // PRE-AI checks
        privacy.check(context, result);
        wellbeing.check(context, result);

        // POST-AI checks
        fairness.check(context, result, PolicyManager.getPolicy());
        robustness.check(context, result, PolicyManager.getPolicy());
        transparency.check(context, result, PolicyManager.getPolicy());
        human.check(context, result);

        // RELATIONAL override
        if (FeedbackService.hasCriticalFeedback()) {
            result.addViolation("Blocked due to critical stakeholder feedback");
        }

        // FINAL accountability
        accountability.check(context, result);

        return result;
    }
}
