package model;

/**
 * Represents user data with privacy attributes
 * Used for privacy governance checks
 */
public class UserData {
    private String name;
    private String email;
    private boolean containsSensitiveData;
    private boolean consentGiven;
    
    public UserData(String name, String email, boolean containsSensitiveData, boolean consentGiven) {
        this.name = name;
        this.email = email;
        this.containsSensitiveData = containsSensitiveData;
        this.consentGiven = consentGiven;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public boolean containsSensitiveData() {
        return containsSensitiveData;
    }
    
    public boolean isConsentGiven() {
        return consentGiven;
    }
    
    /**
     * Creates a copy with sensitive data masked
     */
    public UserData maskedCopy() {
        return new UserData(
            maskString(name),
            maskString(email),
            containsSensitiveData,
            consentGiven
        );
    }
    
    private String maskString(String value) {
        if (value == null || value.length() <= 2) {
            return "***";
        }
        return value.charAt(0) + "***" + value.charAt(value.length() - 1);
    }
}
