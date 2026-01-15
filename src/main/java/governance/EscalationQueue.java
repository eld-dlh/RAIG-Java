package governance;

import core.EthicsContext;
import core.EthicsResult;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Human oversight workflow queue for escalated decisions
 * Implements the paper's event-driven escalation mechanism
 */
public class EscalationQueue {
    
    private final Queue<EscalatedDecision> queue = new ConcurrentLinkedQueue<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final int maxQueueSize;
    
    public EscalationQueue(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }
    
    public EscalatedDecision escalate(EthicsContext context, EthicsResult result, String reason) {
        if (queue.size() >= maxQueueSize) {
            throw new IllegalStateException("Escalation queue is full");
        }
        
        EscalatedDecision escalated = new EscalatedDecision(
            idGenerator.incrementAndGet(),
            context,
            result,
            reason,
            LocalDateTime.now()
        );
        
        queue.offer(escalated);
        return escalated;
    }
    
    public Optional<EscalatedDecision> poll() {
        return Optional.ofNullable(queue.poll());
    }
    
    public int size() {
        return queue.size();
    }
    
    public List<EscalatedDecision> getAll() {
        return new ArrayList<>(queue);
    }
    
    public static class EscalatedDecision {
        private final long id;
        private final EthicsContext context;
        private final EthicsResult result;
        private final String reason;
        private final LocalDateTime escalatedAt;
        private LocalDateTime reviewedAt;
        private Role reviewedBy;
        private boolean humanApproved;
        private String reviewNotes;
        
        public EscalatedDecision(long id, EthicsContext context, EthicsResult result, 
                                String reason, LocalDateTime escalatedAt) {
            this.id = id;
            this.context = context;
            this.result = result;
            this.reason = reason;
            this.escalatedAt = escalatedAt;
        }
        
        public void resolve(Role reviewer, boolean approved, String notes) {
            this.reviewedAt = LocalDateTime.now();
            this.reviewedBy = reviewer;
            this.humanApproved = approved;
            this.reviewNotes = notes;
        }
        
        public long getId() { return id; }
        public EthicsContext getContext() { return context; }
        public EthicsResult getResult() { return result; }
        public String getReason() { return reason; }
        public LocalDateTime getEscalatedAt() { return escalatedAt; }
        public boolean isResolved() { return reviewedAt != null; }
        public boolean isHumanApproved() { return humanApproved; }
    }
}
