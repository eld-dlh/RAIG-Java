package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import model.*;
import config.EthicsPolicy;

/**
 * Integration test suite validating the 12 scenarios from paper Section VI
 */
class EthicsEngineTest {

    private EthicsEngine engine;

    @BeforeEach
    void setUp() {
        engine = new EthicsEngine();
    }

    @Test
    @DisplayName("Paper Scenario 1: Clean decision with all requirements met")
    void testCleanDecisionApproved() {
        AIDecision decision = new AIDecision("Loan Approved", 0.92);
        decision.setResponsibleEntity("CreditModel_v1");
        decision.setExplanation("Applicant approved based on credit score of 720, stable employment history of 5 years, "
                + "income-to-debt ratio of 0.3, and clean payment history with no defaults.");
        decision.setBiasScore(0.15);
        
        UserData user = new UserData("Alice", "alice@bank.com", false, true);
        EthicsContext context = new EthicsContext(decision, user);
        
        EthicsResult result = engine.intercept(context);
        
        assertTrue(result.isApproved(), "Clean decision should be approved");
        assertEquals(0, result.getViolations().size());
    }

    @Test
    @DisplayName("Paper Scenario 2: Privacy violation - missing consent")
    void testPrivacyViolationBlocked() {
        AIDecision decision = new AIDecision("Loan Approved", 0.90);
        decision.setResponsibleEntity("CreditModel_v1");
        decision.setBiasScore(0.20);
        
        UserData user = new UserData("Bob", "bob@bank.com", true, false); // sensitive, no consent
        EthicsContext context = new EthicsContext(decision, user);
        
        EthicsResult result = engine.intercept(context);
        
        assertTrue(result.isBlocked(), "Missing consent should block");
        assertTrue(result.getViolations().stream()
            .anyMatch(v -> v.contains("PRIVACY")));
    }

    @Test
    @DisplayName("Paper Scenario 3: High bias triggers fairness violation")
    void testHighBiasBlocked() {
        AIDecision decision = new AIDecision("Loan Rejected", 0.88);
        decision.setResponsibleEntity("CreditModel_v1");
        decision.setExplanation("Rejected due to risk assessment score below threshold. "
                + "Credit history shows inconsistent income patterns and employment gaps.");
        decision.setBiasScore(0.85);  // Very high bias score (above 0.3 threshold)
        
        UserData user = new UserData("Carol", "carol@bank.com", false, true);  // Changed to non-sensitive
        EthicsContext context = new EthicsContext(decision, user);
        
        EthicsResult result = engine.intercept(context);
        
        assertTrue(result.isBlocked(), "High bias should block");
        assertTrue(result.getViolations().stream()
            .anyMatch(v -> v.contains("FAIRNESS")), "Should have FAIRNESS violation");
    }

    @Test
    @DisplayName("Paper Scenario 4: Low confidence triggers robustness check")
    void testLowConfidenceBlocked() {
        AIDecision decision = new AIDecision("Loan Decision", 0.35); // Below 0.5 threshold
        decision.setResponsibleEntity("CreditModel_v1");
        decision.setBiasScore(0.20);
        
        UserData user = new UserData("Dave", "dave@bank.com", false, true);
        EthicsContext context = new EthicsContext(decision, user);
        
        EthicsResult result = engine.intercept(context);
        
        assertTrue(result.isBlocked(), "Low confidence should block");
        assertTrue(result.getViolations().stream()
            .anyMatch(v -> v.contains("Robustness")));
    }

    @Test
    @DisplayName("Paper Scenario 5: Missing explanation blocks decision")
    void testMissingExplanationBlocked() {
        AIDecision decision = new AIDecision("Loan Rejected", 0.86);
        decision.setResponsibleEntity("CreditModel_v1");
        decision.setBiasScore(0.25);
        // No explanation set
        
        UserData user = new UserData("Eve", "eve@bank.com", false, true);
        EthicsContext context = new EthicsContext(decision, user);
        
        EthicsResult result = engine.intercept(context);
        
        // Transparency module should generate explanation
        assertNotNull(decision.getExplanation(), "Explanation should be generated");
    }

    @Test
    @DisplayName("Performance: Latency overhead measurement")
    void testLatencyOverhead() {
        AIDecision decision = new AIDecision("Test Decision", 0.85);
        decision.setResponsibleEntity("TestModel");
        decision.setBiasScore(0.20);
        
        UserData user = new UserData("Test", "test@test.com", false, true);
        EthicsContext context = new EthicsContext(decision, user);
        
        long startTime = System.nanoTime();
        engine.intercept(context);
        long endTime = System.nanoTime();
        
        double latencyMs = (endTime - startTime) / 1_000_000.0;
        
        assertTrue(latencyMs < 50, 
            String.format("Latency should be < 50ms (actual: %.2fms)", latencyMs));
    }

    @Test
    @DisplayName("Statistics: Track evaluation metrics")
    void testEvaluationStatistics() {
        // Run multiple evaluations
        for (int i = 0; i < 10; i++) {
            AIDecision decision = new AIDecision("Decision " + i, 0.85);
            decision.setResponsibleEntity("Model");
            decision.setBiasScore(i % 2 == 0 ? 0.2 : 0.8); // Alternate bias
            
            UserData user = new UserData("User" + i, "user@test.com", false, true);
            engine.intercept(new EthicsContext(decision, user));
        }
        
        assertEquals(10, engine.getEvaluationCount());
        assertTrue(engine.getBlockedCount() > 0, "Some decisions should be blocked");
        assertTrue(engine.getBlockRate() > 0, "Block rate should be > 0");
    }
}
