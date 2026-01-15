package governance;

import java.util.*;

/**
 * Manages role assignments and permissions
 */
public class RoleManager {
    private Map<String, Set<Role>> userRoles;
    
    public RoleManager() {
        this.userRoles = new HashMap<>();
        initializeDefaultRoles();
    }
    
    private void initializeDefaultRoles() {
        // Set up default test users
        assignRole("admin@system.com", Role.ADMIN);
        assignRole("ethics@system.com", Role.ETHICS_OFFICER);
        assignRole("ai-reviewer@system.com", Role.AI_REVIEWER);
    }
    
    public void assignRole(String userId, Role role) {
        userRoles.computeIfAbsent(userId, k -> new HashSet<>()).add(role);
    }
    
    public boolean hasRole(String userId, Role role) {
        return userRoles.getOrDefault(userId, Collections.emptySet()).contains(role);
    }
    
    public Set<Role> getRoles(String userId) {
        return userRoles.getOrDefault(userId, Collections.emptySet());
    }
}
