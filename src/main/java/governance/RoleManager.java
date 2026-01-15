package governance;

import java.util.HashMap;
import java.util.Map;

public class RoleManager {

    private Map<String, Role> userRoles = new HashMap<>();

    public RoleManager() {
        // Default roles for testing
        userRoles.put("admin@example.com", Role.ADMIN);
        userRoles.put("ethics@example.com", Role.ETHICS_OFFICER);
        userRoles.put("user@example.com", Role.USER);
    }

    public boolean hasRole(String userEmail, Role role) {
        Role userRole = userRoles.getOrDefault(userEmail, Role.USER);
        return userRole == role;
    }

    public static boolean canApprove(Role role) {
        return role == Role.ETHICS_OFFICER || role == Role.ADMIN;
    }

    public static boolean canDeploy(Role role) {
        return role == Role.ADMIN;
    }
}
