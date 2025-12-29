package governance;

public class RoleManager {

    public static boolean canApprove(Role role) {
        return role == Role.ETHICS_OFFICER || role == Role.ADMIN;
    }

    public static boolean canDeploy(Role role) {
        return role == Role.ADMIN;
    }
}
