package data;

import core.EthicsResult;
import core.EthicsDecision;
import java.util.*;

/**
 * Records decision history for audit trail and analytics
 */
public class DecisionHistory {
    private List<HistoryEntry> entries;
    
    public DecisionHistory() {
        this.entries = new ArrayList<>();
    }
    
    public void record(EthicsResult result) {
        entries.add(new HistoryEntry(result, System.currentTimeMillis()));
    }
    
    public List<HistoryEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    public int getTotalCount() {
        return entries.size();
    }
    
    public int getApprovedCount() {
        return (int) entries.stream()
            .filter(e -> e.decision == EthicsDecision.APPROVE)
            .count();
    }
    
    public int getBlockedCount() {
        return (int) entries.stream()
            .filter(e -> e.decision == EthicsDecision.BLOCK)
            .count();
    }
    
    public int getEscalatedCount() {
        return (int) entries.stream()
            .filter(e -> e.decision == EthicsDecision.ESCALATE)
            .count();
    }
    
    public static class HistoryEntry {
        public final EthicsDecision decision;
        public final long timestamp;
        public final int violationCount;
        public final int warningCount;
        
        HistoryEntry(EthicsResult result, long timestamp) {
            this.decision = result.getFinalDecision();
            this.timestamp = timestamp;
            this.violationCount = result.getViolations().size();
            this.warningCount = result.getWarnings().size();
        }
    }
}
