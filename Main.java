package main;

import core.EthicsEngine;
import core.EthicsContext;
import config.EthicsPolicy;
import governance.ApprovalWorkflow;
import governance.RoleManager;
import governance.Role;
import model.AIDecision;
import model.UserData;
import result.EthicsResult;

public class Main {

    public static void main(String[] args) {

        // ---------------- Framework bootstrap ----------------
        EthicsPolicy policy = EthicsPolicy.defaultPolicy();
        RoleManager roleManager = new RoleManager();
        EthicsEngine engine = new EthicsEngine(policy);
        ApprovalWorkflow workflow = new ApprovalWorkflow(roleManager);

        /* ---------------- Scenario 1: Clean decision ---------------- */
        System.out.println("\n=== Scenario 1: Clean decision ===");

        AIDecision d1 = new AIDecision("Loan Approved", 0.92);
        d1.setResponsibleEntity("CreditModel_v1");
        d1.setExplanation("Applicant met all eligibility criteria");

        UserData u1 = new UserData("Alice", "alice@bank.com", true, false);

        runScenario(engine, workflow, d1, u1);

        /* ---------------- Scenario 2: Privacy violation ---------------- */
        System.out.println("\n=== Scenario 2: Privacy violation ===");

        AIDecision d2 = new AIDecision("Loan Approved", 0.90);
        d2.setResponsibleEntity("CreditModel_v1");
        d2.setExplanation("Automated approval based on score");

        // No user consent → privacy failure
        UserData u2 = new UserData("Bob", "bob@bank.com", false, true);

        runScenario(engine, workflow, d2, u2);

        /* ---------------- Scenario 3: High bias ---------------- */
        System.out.println("\n=== Scenario 3: High bias ===");

        AIDecision d3 = new AIDecision("Loan Rejected", 0.88);
        d3.setResponsibleEntity("CreditModel_v1");

        /*
         * NOTE:
         * For demo purposes, bias is injected directly via setBiasScore(...)
         * to simulate an unfair outcome. In a full deployment, this value
         * would be computed dynamically by the FairnessModule using
         * an external tool such as TrustyAI.
         */
        d3.setBiasScore(0.85); // Simulated unfair outcome

        UserData u3 = new UserData("Carol", "carol@bank.com", true, true);

        runScenario(engine, workflow, d3, u3);

        /* ---------------- Scenario 4: Missing explanation ---------------- */
        System.out.println("\n=== Scenario 4: Missing transparency ===");

        AIDecision d4 = new AIDecision("Loan Rejected", 0.86);
        d4.setResponsibleEntity("CreditModel_v1");
        // No explanation provided → transparency violation

        UserData u4 = new UserData("Dave", "dave@bank.com", true, false);

        runScenario(engine, workflow, d4, u4);
    }

    /**
     * Simulates the complete AI governance workflow for a single decision:
     * 1) Builds the ethics context (decision + user data)
     * 2) Runs the decision through the EthicsEngine (all 7 pillars)
     * 3) Passes the EthicsResult to the ApprovalWorkflow
     * 4) Prints both the ethics outcome and the final approval decision
     *
     * This method demonstrates how ethical violations
     * affect downstream governance decisions.
     */
    private static void runScenario(EthicsEngine engine,
                                    ApprovalWorkflow workflow,
                                    AIDecision decision,
                                    UserData userData) {

        EthicsContext context = new EthicsContext(decision, userData);
        EthicsResult result = engine.intercept(context);

        boolean approved = workflow.approve(result, Role.ETHICS_OFFICER);

        System.out.println(result);
        System.out.println("Final approval: " +
                (approved ? "APPROVED" : "BLOCKED"));
    }
}
