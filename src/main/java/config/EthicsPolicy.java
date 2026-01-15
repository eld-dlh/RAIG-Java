package config;

/**
 * Configuration for ethics policy thresholds
 * As described in paper Section IV.B
 */
public class EthicsPolicy {
    // Fairness thresholds
    public double maxBias = 0.3;
    
    // Robustness thresholds
    public double minConfidence = 0.3;
    public double escalationConfidenceThreshold = 0.6;
    
    // Privacy settings
    public boolean requireConsent = true;
    public boolean enforceDataMinimization = true;
    
    // Transparency settings
    public boolean requireExplanation = true;
    
    // Accountability settings
    public boolean requireResponsibleEntity = true;
    
    /**
     * Returns a default policy with standard thresholds
     */
    public static EthicsPolicy defaultPolicy() {
        return new EthicsPolicy();
    }
    
    /**
     * Creates a strict policy with tighter constraints
     */
    public static EthicsPolicy strictPolicy() {
        EthicsPolicy policy = new EthicsPolicy();
        policy.maxBias = 0.2;
        policy.minConfidence = 0.7;
        policy.escalationConfidenceThreshold = 0.8;
        return policy;
    }
    
    /**
     * Creates a lenient policy for testing
     */
    public static EthicsPolicy lenientPolicy() {
        EthicsPolicy policy = new EthicsPolicy();
        policy.maxBias = 0.5;
        policy.minConfidence = 0.3;
        policy.requireExplanation = false;
        return policy;
    }
}
