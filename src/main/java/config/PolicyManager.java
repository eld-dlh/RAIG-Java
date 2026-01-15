package config;

/**
 * Manages the active ethics policy configuration
 */
public class PolicyManager {
    private static EthicsPolicy activePolicy = EthicsPolicy.defaultPolicy();
    
    public static EthicsPolicy getPolicy() {
        return activePolicy;
    }
    
    public static void setPolicy(EthicsPolicy policy) {
        activePolicy = policy;
    }
    
    public static void reset() {
        activePolicy = EthicsPolicy.defaultPolicy();
    }
}
