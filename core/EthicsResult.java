package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the outcome of an ethics evaluation.
 * This is a pure data object (no business logic).
 */
public class EthicsResult {

    private final List<String> violations = new ArrayList<>();

    /* ---------------- Mutators ---------------- */

    public void addViolation(String violation) {
        violations.add(violation);
    }

    /* ---------------- Accessors ---------------- */

    public List<String> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public boolean hasViolations() {
        return !violations.isEmpty();
    }

    /**
     * Semantic alias for governance workflows.
     */
    public boolean isBlocked() {
        return hasViolations();
    }

    /* ---------------- Debug / Demo ---------------- */

    @Override
    public String toString() {
        if (violations.isEmpty()) {
            return "EthicsResult{PASS – no violations}";
        }
        return "EthicsResult{FAIL – violations=" + violations + "}";
    }
}
