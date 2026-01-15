package pillars.fairness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import core.*;
import config.EthicsPolicy;
import model.*;

/**
 * Test suite for FairnessModule as described in paper Section VI
 */
class FairnessModuleTest {

    private FairnessModule module;
    private EthicsPolicy policy;

    @BeforeEach
    void setUp() {
        module = new FairnessModule();
        policy = EthicsPolicy.defaultPolicy();
    }

    @Test
    @DisplayName("Scenario 1: Low bias decision should be approved")
    void testLowBiasApproved() {
        AIDecision decision = new AIDecision("Loan Approved", 0.85);
        decision.setBiasScore(0.15);
        decision.setResponsibleEntity("CreditModel_v1");
        
        UserData user = new UserData("Alice", "alice@test.com", false, true);
        EthicsContext context = new EthicsContext(decision, user);
        EthicsResult result = new EthicsResult();
        
        module.check(context, result, policy);
        
        assertFalse(result.isBlocked(), "Low bias should not block decision");
        assertEquals(0, result.getViolations().size());
    }

    @Test
    @DisplayName("Scenario 2: High bias (0.85) should trigger fairness violation")
    void testHighBiasBlocked() {
        AIDecision decision = new AIDecision("Loan Rejected", 0.88);
        decision.setBiasScore(0.85);
        decision.setResponsibleEntity("CreditModel_v1");
        
        UserData user = new UserData("Bob", "bob@test.com", false, true);
        EthicsContext context = new EthicsContext(decision, user);
        EthicsResult result = new EthicsResult();
        
        module.check(context, result, policy);
        
        assertTrue(result.isBlocked(), "High bias should block decision");
        assertEquals(1, result.getViolations().size());
        assertTrue(result.getViolations().get(0).contains("FAIRNESS"));
    }

    @Test
    @DisplayName("Scenario 3: Auto-computed bias from TrustyAI")
    void testAutoComputedBias() {
        AIDecision decision = new AIDecision("Insurance Quote", 0.90);
        // No bias score set - should be computed
        decision.setResponsibleEntity("InsuranceAI");
        
        UserData user = new UserData("Carol", "carol@test.com", false, true);
        EthicsContext context = new EthicsContext(decision, user);
        EthicsResult result = new EthicsResult();
        
        module.check(context, result, policy);
        
        // TrustyAI returns 0.42, which exceeds 0.3 threshold
        assertTrue(decision.getBiasScore() > 0, "Bias should be computed");
        assertTrue(result.isBlocked(), "Auto-computed high bias should block");
    }
}
