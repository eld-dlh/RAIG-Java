package model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a single AI-generated decision
 * flowing through the Trustworthy AI Governance Framework.
 */
public class AIDecision {

    /* ---------------- Core Decision ---------------- */
    private String decisionLabel;           // e.g., "Loan Approved"
    private double confidence;               // Model confidence score (0–1)

    /* ---------------- Ethics Metrics ---------------- */
    private double biasScore;                // From TrustyAI / Spark
    private String explanation;              // Human-readable explanation

    /* ---------------- Governance ---------------- */
    private String responsibleEntity;        // Accountability (team/model owner)
    private boolean humanOverrideAvailable;  // Human agency

    /* ---------------- Impact Assessment ---------------- */
    private boolean negativeSocialImpact;    // Well-being pillar

    /* ---------------- Traceability ---------------- */
    private final String decisionId;
    private final LocalDateTime timestamp;

    /* ---------------- Integration Hooks ---------------- */
    private Object model;                    // DL4J model reference
    private Object dataset;                  // Spark dataset reference

    /* ---------------- Constructor ---------------- */
    public AIDecision(String decisionLabel, double confidence) {
        this.decisionLabel = decisionLabel;
        this.confidence = confidence;

        this.decisionId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();

        // Sensible defaults
        this.humanOverrideAvailable = true;
        this.negativeSocialImpact = false;
        this.biasScore = 0.0;
    }

    /* ---------------- Getters & Setters ---------------- */

    public String getDecisionLabel() {
        return decisionLabel;
    }

    public double getConfidence() {
        return confidence;
    }

    public double getBiasScore() {
        return biasScore;
    }

    public void setBiasScore(double biasScore) {
        this.biasScore = biasScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getResponsibleEntity() {
        return responsibleEntity;
    }

    public void setResponsibleEntity(String responsibleEntity) {
        this.responsibleEntity = responsibleEntity;
    }

    public boolean isHumanOverrideAvailable() {
        return humanOverrideAvailable;
    }

    public void setHumanOverrideAvailable(boolean humanOverrideAvailable) {
        this.humanOverrideAvailable = humanOverrideAvailable;
    }

    public boolean hasNegativeSocialImpact() {
        return negativeSocialImpact;
    }

    public void setNegativeSocialImpact(boolean negativeSocialImpact) {
        this.negativeSocialImpact = negativeSocialImpact;
    }

    public String getDecisionId() {
        return decisionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /* ---------------- Integration Hooks ---------------- */

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    public Object getDataset() {
        return dataset;
    }

    public void setDataset(Object dataset) {
        this.dataset = dataset;
    }

    /* ---------------- Audit-Friendly Output ---------------- */
    @Override
    public String toString() {
        return "AIDecision{" +
                "decisionId='" + decisionId + '\'' +
                ", decisionLabel='" + decisionLabel + '\'' +
                ", confidence=" + confidence +
                ", biasScore=" + biasScore +
                ", responsibleEntity='" + responsibleEntity + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
