package communication;

import java.util.ArrayList;
import java.util.List;

public class FeedbackService {

    private static List<StakeholderFeedback> feedbackList = new ArrayList<>();

    public static void submitFeedback(StakeholderFeedback feedback) {
        feedbackList.add(feedback);
    }

    public static boolean hasCriticalFeedback() {
        return feedbackList.stream().anyMatch(f -> f.getSeverity() >= 4);
    }
}
