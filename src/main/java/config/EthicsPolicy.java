package config;

public class EthicsPolicy {
    public double maxBias;
    public double minConfidence;
    public boolean requireExplanation;

    public EthicsPolicy(double maxBias, double minConfidence, boolean requireExplanation) {
        this.maxBias = maxBias;
        this.minConfidence = minConfidence;
        this.requireExplanation = requireExplanation;
    }

    public static EthicsPolicy defaultPolicy() {
        return new EthicsPolicy(0.3, 0.5, true);
    }
}
