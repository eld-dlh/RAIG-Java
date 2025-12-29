import core.*;
import governance.*;
import model.*;

public class Main {
    public static void main(String[] args) {

        AIDecision decision = new AIDecision();
        UserData data = new UserData(true, true);

        EthicsContext context = new EthicsContext(decision, data);
        EthicsEngine engine = new EthicsEngine();

        EthicsResult result = engine.intercept(context);

        ApprovalWorkflow workflow = new ApprovalWorkflow();
        boolean approved = workflow.approve(result, Role.ETHICS_OFFICER);

        System.out.println(approved
                ? "✅ Approved for deployment"
                : "❌ Deployment blocked");
    }
}
