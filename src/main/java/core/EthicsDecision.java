package core;

/**
 * Enum representing the final ethics decision outcome
 * As described in paper Section IV.A
 */
public enum EthicsDecision {
    /**
     * Decision meets all ethical requirements and is approved
     */
    APPROVE,
    
    /**
     * Decision violates ethical policies and is blocked
     */
    BLOCK,
    
    /**
     * Decision requires human review due to borderline metrics or warnings
     */
    ESCALATE
}
