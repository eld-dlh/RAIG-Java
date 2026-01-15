package core;

/**
 * Three-state decision outcome for ethics evaluation
 * As described in the paper's enforcement architecture
 */
public enum EthicsDecision {
    /**
     * Decision passes all ethics checks and can proceed
     */
    APPROVE,
    
    /**
     * Decision violates one or more ethics policies and must be blocked
     */
    BLOCK,
    
    /**
     * Decision is ambiguous or borderline - requires human review
     */
    ESCALATE;
    
    public boolean isApproved() {
        return this == APPROVE;
    }
    
    public boolean isBlocked() {
        return this == BLOCK;
    }
    
    public boolean requiresHumanReview() {
        return this == ESCALATE;
    }
}
