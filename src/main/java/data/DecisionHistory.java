package data;

import model.AIDecision;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Historical decision storage for fairness analysis
 * Implements the paper's group-based statistical metrics
 */
public class DecisionHistory {
    
    private final Map<String, List<HistoricalDecision>> decisions = new ConcurrentHashMap<>();
    private final int maxHistorySize;
    
    public DecisionHistory(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }
    
    public void record(AIDecision decision, boolean approved, Map<String, String> demographics) {
        String key = "all";
        HistoricalDecision record = new HistoricalDecision(
            decision, approved, demographics, LocalDateTime.now()
        );
        
        decisions.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        
        // Maintain size limit
        List<HistoricalDecision> history = decisions.get(key);
        if (history.size() > maxHistorySize) {
            history.remove(0);
        }
    }
    
    public List<HistoricalDecision> getRecent(int count) {
        List<HistoricalDecision> all = decisions.getOrDefault("all", new ArrayList<>());
        int start = Math.max(0, all.size() - count);
        return all.subList(start, all.size());
    }
    
    public Map<String, Double> computeApprovalRates(String protectedAttribute, int sampleSize) {
        List<HistoricalDecision> recent = getRecent(sampleSize);
        
        Map<String, List<Boolean>> groupedByAttribute = recent.stream()
            .filter(d -> d.demographics.containsKey(protectedAttribute))
            .collect(Collectors.groupingBy(
                d -> d.demographics.get(protectedAttribute),
                Collectors.mapping(d -> d.approved, Collectors.toList())
            ));
        
        Map<String, Double> approvalRates = new HashMap<>();
        for (Map.Entry<String, List<Boolean>> entry : groupedByAttribute.entrySet()) {
            long approvedCount = entry.getValue().stream().filter(b -> b).count();
            double rate = (double) approvedCount / entry.getValue().size();
            approvalRates.put(entry.getKey(), rate);
        }
        
        return approvalRates;
    }
    
    public double computeDemographicParity(String protectedAttribute, int sampleSize) {
        Map<String, Double> rates = computeApprovalRates(protectedAttribute, sampleSize);
        
        if (rates.isEmpty()) {
            return 0.0;
        }
        
        double maxRate = Collections.max(rates.values());
        double minRate = Collections.min(rates.values());
        
        return maxRate - minRate;
    }
    
    public int size() {
        return decisions.getOrDefault("all", new ArrayList<>()).size();
    }
    
    public static class HistoricalDecision {
        public final AIDecision decision;
        public final boolean approved;
        public final Map<String, String> demographics;
        public final LocalDateTime timestamp;
        
        public HistoricalDecision(AIDecision decision, boolean approved, 
                                 Map<String, String> demographics, LocalDateTime timestamp) {
            this.decision = decision;
            this.approved = approved;
            this.demographics = demographics;
            this.timestamp = timestamp;
        }
    }
}
