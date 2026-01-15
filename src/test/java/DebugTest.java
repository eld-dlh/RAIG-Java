import core.*;
import model.*;
import config.PolicyManager;

public class DebugTest {
    public static void main(String[] args) {
        EthicsEngine engine = new EthicsEngine();
        
        // Test case: High bias
        AIDecision decision = new AIDecision("Loan Rejected", 0.88);
        decision.setResponsibleEntity("CreditModel_v1");
        decision.setExplanation("Rejected due to risk assessment score below threshold. "
                + "Credit history shows inconsistent income patterns and employment gaps.");
        decision.setBiasScore(0.85);  // Very high bias
        
        UserData user = new UserData("Carol", "carol@bank.com", true, true);
        EthicsContext context = new EthicsContext(decision, user);
        
        EthicsResult result = engine.intercept(context);
        
        System.out.println("=== HIGH BIAS TEST ===");
        System.out.println("Decision: " + result.getFinalDecision());
        System.out.println("Is Blocked: " + result.isBlocked());
        System.out.println("Violations count: " + result.getViolations().size());
        System.out.println("\nViolations:");
        result.getViolations().forEach(v -> System.out.println("  - " + v));
        System.out.println("\nWarnings:");
        result.getWarnings().forEach(w -> System.out.println("  - " + w));
        System.out.println("\nPolicy maxBias: " + PolicyManager.getPolicy().maxBias);
        System.out.println("Decision biasScore: " + decision.getBiasScore());
        
        // Check for FAIRNESS violation
        boolean hasFairnessViolation = result.getViolations().stream()
            .anyMatch(v -> v.contains("FAIRNESS"));
        System.out.println("\nHas FAIRNESS violation: " + hasFairnessViolation);
    }
}
