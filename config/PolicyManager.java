package config;

public class PolicyManager {

    private static EthicsPolicy currentPolicy =
            new EthicsPolicy(0.6, 0.5, true);

    public static EthicsPolicy getPolicy() {
        return currentPolicy;
    }

    // Ethics Committee can update this
    public static void updatePolicy(EthicsPolicy newPolicy) {
        currentPolicy = newPolicy;
    }
}
