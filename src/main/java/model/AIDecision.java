package model;

/**
 * Represents an AI decision with metadata required for ethical evaluation
 * As described in paper Section IV
 */
public class AIDecision {
    private String decisionLabel;
    private double confidence;
    private String responsibleEntity;
    private String explanation;
    private double biasScore;
    private boolean negativeSocialImpact;
    
    public AIDecision(String decisionLabel, double confidence) {
        this.decisionLabel = decisionLabel;
        this.confidence = confidence;
        this.biasScore = -1.0; // Not set
        this.negativeSocialImpact = false;
    }
    
    // Getters
    public String getDecisionLabel() {
        return decisionLabel;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public String getResponsibleEntity() {
        return responsibleEntity;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public double getBiasScore() {
        return biasScore;
    }
    
    public boolean hasNegativeSocialImpact() {
        return negativeSocialImpact;
    }
    
    // Setters
    public void setResponsibleEntity(String responsibleEntity) {
        this.responsibleEntity = responsibleEntity;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public void setBiasScore(double biasScore) {
        this.biasScore = biasScore;
    }
    
    public void setNegativeSocialImpact(boolean negativeSocialImpact) {
        this.negativeSocialImpact = negativeSocialImpact;
    }
}
