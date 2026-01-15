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

/**
 * Implements Algorithm 1 from the paper: Ethics Engine with fail-fast logic
 * Orchestrates evaluation across all policy modules and determines enforcement
 */
public class EthicsEngine {

    private PrivacyGovernanceModule privacy = new PrivacyGovernanceModule();
    private WellBeingModule wellbeing = new WellBeingModule();
    private FairnessModule fairness = new FairnessModule();
    private RobustnessSafetyModule robustness = new RobustnessSafetyModule();
    private TransparencyModule transparency = new TransparencyModule();
    private HumanOversightModule human = new HumanOversightModule();
    private AccountabilityModule accountability = new AccountabilityModule();
    
    private boolean failFast = true;
    private boolean auditAllDecisions = true;
    
    private long evaluationCount = 0;
    private long blockedCount = 0;
    private long escalatedCount = 0;

    /**
     * Algorithm 1: Intercept and evaluate AI decision
     * Returns: APPROVE, BLOCK, or ESCALATE
     */
    public EthicsResult intercept(EthicsContext context) {
        evaluationCount++;
        long startTime = System.nanoTime();

        EthicsResult result = new EthicsResult();

        try {
            // PRE-AI checks (fail-fast on critical violations)
            privacy.check(context, result);
            if (failFast && result.isBlocked()) {
                return logAndReturn(result, context, startTime);
            }
            
            wellbeing.check(context, result);
            if (failFast && result.isBlocked()) {
                return logAndReturn(result, context, startTime);
            }

            // POST-AI checks
            fairness.check(context, result, PolicyManager.getPolicy());
            if (failFast && result.isBlocked()) {
                return logAndReturn(result, context, startTime);
            }
            
            robustness.check(context, result, PolicyManager.getPolicy());
            if (failFast && result.isBlocked()) {
                return logAndReturn(result, context, startTime);
            }
            
            transparency.check(context, result, PolicyManager.getPolicy());
            if (failFast && result.isBlocked()) {
                return logAndReturn(result, context, startTime);
            }
            
            human.check(context, result);
            if (failFast && result.isBlocked()) {
                return logAndReturn(result, context, startTime);
            }

            // RELATIONAL override
            if (FeedbackService.hasCriticalFeedback()) {
                result.addViolation("Blocked due to critical stakeholder feedback");
            }

            // FINAL accountability
            accountability.check(context, result);
            
            return logAndReturn(result, context, startTime);
            
        } catch (Exception e) {
            result.addViolation("SYSTEM: Unexpected error during evaluation - " + e.getMessage());
            return logAndReturn(result, context, startTime);
        }
    }
    
    private EthicsResult logAndReturn(EthicsResult result, EthicsContext context, long startTime) {
        long latencyNanos = System.nanoTime() - startTime;
        double latencyMs = latencyNanos / 1_000_000.0;
        
        if (result.isBlocked()) {
            blockedCount++;
        } else if (result.requiresEscalation()) {
            escalatedCount++;
        }
        
        if (auditAllDecisions) {
            logAuditTrail(context, result, latencyMs);
        }
        
        return result;
    }
    
    private void logAuditTrail(EthicsContext context, EthicsResult result, double latencyMs) {
        // In production, this would write to structured audit log
        System.out.printf("[AUDIT] Decision: %s | Result: %s | Latency: %.2fms | Violations: %d%n",
            context.decision.getDecisionLabel(),
            result.getFinalDecision(),
            latencyMs,
            result.getViolations().size()
        );
    }
    
    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }
    
    public long getEvaluationCount() { return evaluationCount; }
    public long getBlockedCount() { return blockedCount; }
    public long getEscalatedCount() { return escalatedCount; }
    public double getBlockRate() { 
        return evaluationCount > 0 ? (double) blockedCount / evaluationCount : 0.0;
    }
    public double getEscalationRate() { 
        return evaluationCount > 0 ? (double) escalatedCount / evaluationCount : 0.0;
    }
}
