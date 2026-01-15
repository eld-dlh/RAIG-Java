package core;

import config.EthicsPolicy;
import config.PolicyManager;
import pillars.accountability.AccountabilityModule;
import pillars.fairness.FairnessModule;
import pillars.human.HumanOversightModule;
import pillars.privacy.PrivacyGovernanceModule;
import pillars.robustness.RobustnessSafetyModule;
import pillars.transparency.TransparencyModule;
import pillars.wellbeing.WellBeingModule;

/**
 * Core ethics engine implementing the RAIG framework
 * Orchestrates all seven pillar modules as described in paper Section IV
 */
public class EthicsEngine {
    private AccountabilityModule accountabilityModule;
    private FairnessModule fairnessModule;
    private HumanOversightModule humanOversightModule;
    private PrivacyGovernanceModule privacyModule;
    private RobustnessSafetyModule robustnessModule;
    private TransparencyModule transparencyModule;
    private WellBeingModule wellBeingModule;
    
    // Statistics tracking
    private int evaluationCount = 0;
    private int blockedCount = 0;
    private int escalatedCount = 0;
    
    public EthicsEngine() {
        this.accountabilityModule = new AccountabilityModule();
        this.fairnessModule = new FairnessModule();
        this.humanOversightModule = new HumanOversightModule();
        this.privacyModule = new PrivacyGovernanceModule();
        this.robustnessModule = new RobustnessSafetyModule();
        this.transparencyModule = new TransparencyModule();
        this.wellBeingModule = new WellBeingModule();
    }
    
    /**
     * Main interception point for AI decisions
     * Evaluates decision against all seven ethics pillars
     */
    public EthicsResult intercept(EthicsContext context) {
        evaluationCount++;
        EthicsResult result = new EthicsResult();
        EthicsPolicy policy = PolicyManager.getPolicy();
        
        // Execute all pillar checks
        // Order matters: fail-fast on critical violations
        
        // 1. Privacy - critical, must pass first
        privacyModule.check(context, result);
        if (result.isBlocked()) {
            blockedCount++;
            return result;
        }
        
        // 2. Accountability - required for all decisions
        accountabilityModule.check(context, result, policy);
        
        // 3. Fairness - check for bias
        fairnessModule.check(context, result, policy);
        
        // 4. Robustness - validate technical quality
        robustnessModule.check(context, result, policy);
        
        // 5. Transparency - ensure explainability
        transparencyModule.check(context, result, policy);
        
        // 6. Human Oversight - flag for review if needed
        humanOversightModule.check(context, result, policy);
        
        // 7. Well-being - assess societal impact
        wellBeingModule.check(context, result, policy);
        
        // Update statistics
        if (result.isBlocked()) {
            blockedCount++;
        } else if (result.requiresEscalation()) {
            escalatedCount++;
        }
        
        return result;
    }
    
    // Statistics methods
    public int getEvaluationCount() {
        return evaluationCount;
    }
    
    public int getBlockedCount() {
        return blockedCount;
    }
    
    public int getEscalatedCount() {
        return escalatedCount;
    }
    
    public double getBlockRate() {
        return evaluationCount > 0 ? (double) blockedCount / evaluationCount : 0.0;
    }
    
    public double getEscalationRate() {
        return evaluationCount > 0 ? (double) escalatedCount / evaluationCount : 0.0;
    }
}
