package pillars.privacy;

import core.EthicsContext;
import core.EthicsResult;

public class PrivacyGovernanceModule {

    public void check(EthicsContext context, EthicsResult result) {

        if (context.userData.containsSensitiveData()) {

            if (!context.userData.isConsentGiven()) {
                result.addViolation("PRIVACY: Consent missing");
            }

            // Scrub before AI sees data
            context.userData = context.userData.maskedCopy();
        }
    }
}
