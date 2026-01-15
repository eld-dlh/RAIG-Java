package governance;

/**
 * Defines roles in the ethics approval workflow
 * As described in paper Section IV.C
 */
public enum Role {
    /**
     * Officer responsible for ethics compliance
     */
    ETHICS_OFFICER,
    
    /**
     * Technical reviewer for AI model decisions
     */
    AI_REVIEWER,
    
    /**
     * Legal compliance reviewer
     */
    LEGAL_REVIEWER,
    
    /**
     * Administrative approver for escalated decisions
     */
    ADMIN,
    
    /**
     * System role for automated processes
     */
    SYSTEM
}
