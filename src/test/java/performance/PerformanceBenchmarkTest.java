package performance;

import core.*;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Performance benchmarks to validate research paper claims:
 * - Average latency < 15ms per decision
 * - Concurrent processing capability
 * - Throughput under load
 */
public class PerformanceBenchmarkTest {

    private EthicsEngine engine;
    private static final int WARMUP_ITERATIONS = 100;
    private static final int BENCHMARK_ITERATIONS = 1000;
    private static final int CONCURRENT_THREADS = 10;
    private static final double TARGET_LATENCY_MS = 15.0;

    @BeforeEach
    void setup() {
        engine = new EthicsEngine();
        
        // Warmup: Run several iterations to let JIT optimize
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            evaluateCleanDecision();
        }
    }

    @Test
    @DisplayName("Benchmark 1: Average latency should be under 15ms")
    void testAverageLatency() {
        List<Long> latencies = new ArrayList<>();
        
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            evaluateCleanDecision();
            long end = System.nanoTime();
            
            latencies.add((end - start) / 1_000_000); // Convert to milliseconds
        }
        
        double avgLatency = latencies.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        
        double p50 = percentile(latencies, 50);
        double p95 = percentile(latencies, 95);
        double p99 = percentile(latencies, 99);
        
        System.out.println("\n=== LATENCY BENCHMARK ===");
        System.out.println(String.format("Iterations: %,d", BENCHMARK_ITERATIONS));
        System.out.println(String.format("Average: %.2f ms", avgLatency));
        System.out.println(String.format("P50 (median): %.2f ms", p50));
        System.out.println(String.format("P95: %.2f ms", p95));
        System.out.println(String.format("P99: %.2f ms", p99));
        System.out.println(String.format("Min: %d ms", latencies.stream().min(Long::compare).orElse(0L)));
        System.out.println(String.format("Max: %d ms", latencies.stream().max(Long::compare).orElse(0L)));
        System.out.println(String.format("Target: < %.0f ms", TARGET_LATENCY_MS));
        
        assertTrue(avgLatency < TARGET_LATENCY_MS, 
                String.format("Average latency %.2fms exceeds target of %.0fms", 
                        avgLatency, TARGET_LATENCY_MS));
    }

    @Test
    @DisplayName("Benchmark 2: Concurrent processing capability")
    void testConcurrentProcessing() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(CONCURRENT_THREADS);
        
        List<Future<Long>> futures = new ArrayList<>();
        
        // Submit concurrent tasks
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            futures.add(executor.submit(() -> {
                startLatch.await(); // Wait for all threads to be ready
                
                long start = System.nanoTime();
                
                // Each thread processes multiple decisions
                for (int j = 0; j < 100; j++) {
                    evaluateCleanDecision();
                }
                
                long end = System.nanoTime();
                endLatch.countDown();
                
                return (end - start) / 1_000_000; // Total time in ms
            }));
        }
        
        long overallStart = System.nanoTime();
        startLatch.countDown(); // Start all threads simultaneously
        endLatch.await(30, TimeUnit.SECONDS); // Wait for completion
        long overallEnd = System.nanoTime();
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        double totalTimeMs = (overallEnd - overallStart) / 1_000_000.0;
        int totalDecisions = CONCURRENT_THREADS * 100;
        double throughput = totalDecisions / (totalTimeMs / 1000.0); // decisions per second
        
        System.out.println("\n=== CONCURRENT PROCESSING BENCHMARK ===");
        System.out.println(String.format("Threads: %d", CONCURRENT_THREADS));
        System.out.println(String.format("Decisions per thread: 100"));
        System.out.println(String.format("Total decisions: %,d", totalDecisions));
        System.out.println(String.format("Total time: %.2f ms", totalTimeMs));
        System.out.println(String.format("Throughput: %.0f decisions/second", throughput));
        
        // All threads should complete without errors
        for (Future<Long> future : futures) {
            assertNotNull(future.get(), "Thread should complete successfully");
        }
        
        assertTrue(throughput > 100, "Should process at least 100 decisions per second");
    }

    @Test
    @DisplayName("Benchmark 3: Memory efficiency - no memory leaks")
    void testMemoryEfficiency() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Suggest garbage collection
        
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // Process many decisions
        for (int i = 0; i < 10000; i++) {
            evaluateCleanDecision();
            
            if (i % 1000 == 0) {
                runtime.gc(); // Periodic GC
            }
        }
        
        runtime.gc();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        
        long memoryGrowth = memoryAfter - memoryBefore;
        double memoryGrowthMB = memoryGrowth / (1024.0 * 1024.0);
        
        System.out.println("\n=== MEMORY EFFICIENCY BENCHMARK ===");
        System.out.println(String.format("Decisions processed: 10,000"));
        System.out.println(String.format("Memory before: %.2f MB", memoryBefore / (1024.0 * 1024.0)));
        System.out.println(String.format("Memory after: %.2f MB", memoryAfter / (1024.0 * 1024.0)));
        System.out.println(String.format("Memory growth: %.2f MB", memoryGrowthMB));
        
        // Memory growth should be reasonable (< 50MB for 10k decisions)
        assertTrue(memoryGrowthMB < 50.0, 
                String.format("Memory growth %.2fMB exceeds threshold", memoryGrowthMB));
    }

    @Test
    @DisplayName("Benchmark 4: Different scenario types performance")
    void testScenarioVariations() {
        List<String> scenarioTypes = List.of("APPROVE", "BLOCK", "ESCALATE");
        
        System.out.println("\n=== SCENARIO VARIATION BENCHMARK ===");
        
        for (String scenarioType : scenarioTypes) {
            List<Long> latencies = new ArrayList<>();
            
            for (int i = 0; i < 500; i++) {
                long start = System.nanoTime();
                evaluateScenario(scenarioType);
                long end = System.nanoTime();
                
                latencies.add((end - start) / 1_000_000);
            }
            
            double avgLatency = latencies.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            
            System.out.println(String.format("%-10s: Avg %.2f ms, P95 %.2f ms", 
                    scenarioType, avgLatency, percentile(latencies, 95)));
            
            assertTrue(avgLatency < TARGET_LATENCY_MS * 1.5, // Allow 50% buffer for complex scenarios
                    String.format("%s scenario latency %.2fms exceeds threshold", 
                            scenarioType, avgLatency));
        }
    }

    // Helper methods
    
    private void evaluateCleanDecision() {
        AIDecision decision = new AIDecision("Loan Approved", 0.92);
        decision.setResponsibleEntity("CreditModel_v1");
        decision.setExplanation("Applicant approved based on credit score of 720, stable employment " +
                "history of 5 years, income-to-debt ratio of 0.3, and clean payment history.");
        decision.setBiasScore(0.15);
        
        UserData user = new UserData("Alice", "alice@bank.com", false, true);
        EthicsContext context = new EthicsContext(decision, user);
        
        engine.intercept(context);
    }
    
    private void evaluateScenario(String type) {
        switch (type) {
            case "APPROVE":
                evaluateCleanDecision();
                break;
            case "BLOCK":
                evaluateHighBiasDecision();
                break;
            case "ESCALATE":
                evaluateLowConfidenceDecision();
                break;
        }
    }
    
    private void evaluateHighBiasDecision() {
        AIDecision decision = new AIDecision("Loan Rejected", 0.88);
        decision.setResponsibleEntity("CreditModel_v1");
        decision.setExplanation("Rejected due to risk assessment score below threshold. " +
                "Credit history shows inconsistent income patterns and employment gaps.");
        decision.setBiasScore(0.85);
        
        UserData user = new UserData("Bob", "bob@bank.com", false, true);
        EthicsContext context = new EthicsContext(decision, user);
        
        engine.intercept(context);
    }
    
    private void evaluateLowConfidenceDecision() {
        AIDecision decision = new AIDecision("Loan Approved", 0.45);  // Low confidence
        decision.setResponsibleEntity("CreditModel_v1");
        decision.setExplanation("Applicant meets criteria but confidence is low due to " +
                "limited credit history and recent employment change.");
        decision.setBiasScore(0.20);
        
        UserData user = new UserData("Carol", "carol@bank.com", false, true);
        EthicsContext context = new EthicsContext(decision, user);
        
        engine.intercept(context);
    }
    
    private double percentile(List<Long> values, int percentile) {
        List<Long> sorted = new ArrayList<>(values);
        sorted.sort(Long::compareTo);
        
        int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(index, sorted.size() - 1)));
    }
}
