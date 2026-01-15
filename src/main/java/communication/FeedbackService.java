package communication;

import core.EthicsResult;
import java.util.*;

/**
 * Manages user feedback and notifications about ethics decisions
 */
public class FeedbackService {
    private List<Notification> notifications;
    
    public FeedbackService() {
        this.notifications = new ArrayList<>();
    }
    
    /**
     * Sends notification about a decision
     */
    public void sendNotification(String userId, EthicsResult result) {
        Notification notification = new Notification(
            userId,
            result.getFinalDecision().toString(),
            generateMessage(result),
            System.currentTimeMillis()
        );
        notifications.add(notification);
    }
    
    private String generateMessage(EthicsResult result) {
        if (result.isBlocked()) {
            return "Decision blocked due to ethics violations: " + 
                   String.join(", ", result.getViolations());
        } else if (result.requiresEscalation()) {
            return "Decision escalated for human review: " + result.getEscalationReason();
        } else {
            return "Decision approved by ethics framework";
        }
    }
    
    public List<Notification> getNotifications(String userId) {
        return notifications.stream()
            .filter(n -> n.userId.equals(userId))
            .toList();
    }
    
    public static class Notification {
        public final String userId;
        public final String decisionType;
        public final String message;
        public final long timestamp;
        
        Notification(String userId, String decisionType, String message, long timestamp) {
            this.userId = userId;
            this.decisionType = decisionType;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
