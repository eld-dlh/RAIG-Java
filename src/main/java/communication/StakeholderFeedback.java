package communication;

public class StakeholderFeedback {
    private String userId;
    private String concern;
    private int severity; // 1–5

    public StakeholderFeedback(String userId, String concern, int severity) {
        this.userId = userId;
        this.concern = concern;
        this.severity = severity;
    }

    public int getSeverity() {
        return severity;
    }
}
