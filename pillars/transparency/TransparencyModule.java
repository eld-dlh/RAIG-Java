package pillars.transparency;

import core.EthicsContext;
import core.EthicsResult;
import config.EthicsPolicy;

import java.util.HashMap;
import java.util.Map;

public class TransparencyModule {

    private static final Map<String, String> explanationMap = new HashMap<>();

    static {
        explanationMap.put("FAIRNESS",
                "The model showed unequal outcomes across demographic groups.");
        explanationMap.put("ROBUSTNESS",
                "The model confidence was below the accepted safety threshold.");
        explanationMap.put("PRIVACY",
                "Personal data was used without sufficient anonymization.");
        explanationMap.put("WELLBEING",
                "The decision may negatively impact societal or environmental well-being.");
    }

    public void check(EthicsContext context,
                      EthicsResult result,
                      EthicsPolicy policy) {

        if (!policy.requireExplanation) return;

        if (context.decision.getExplanation() == null) {
            String generatedExplanation = generateExplanation(result);
            context.decision.setExplanation(generatedExplanation);

            if (generatedExplanation == null) {
                result.addViolation("Transparency: Explanation unavailable");
            }
        }
    }

    private String generateExplanation(EthicsResult result) {
        if (result.getViolations().isEmpty()) {
            return "The AI decision complies with all ethical requirements.";
        }

        StringBuilder explanation = new StringBuilder("Decision flagged due to: ");

        for (String violation : result.getViolations()) {
            explanation.append(
                    explanationMap.getOrDefault(
                            violation.split(":")[0].toUpperCase(),
                            violation
                    )
            ).append(" ");
        }
        return explanation.toString();
    }
}
