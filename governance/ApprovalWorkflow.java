package governance;

import core.EthicsResult;

public class ApprovalWorkflow {

    public boolean approve(EthicsResult result, Role role) {
        if (!result.isApproved()) return false;
        return RoleManager.canApprove(role);
    }
}
