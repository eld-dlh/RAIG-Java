package model;

public class UserData {

    private String name;
    private String email;
    private boolean sensitive;
    private boolean consentGiven;

    public UserData(String name, String email,
                    boolean sensitive, boolean consentGiven) {
        this.name = name;
        this.email = email;
        this.sensitive = sensitive;
        this.consentGiven = consentGiven;
    }

    public boolean containsSensitiveData() {
        return sensitive;
    }

    public boolean isConsentGiven() {
        return consentGiven;
    }

    public UserData maskedCopy() {
        return new UserData(
                "REDACTED",
                "REDACTED",
                sensitive,
                consentGiven
        );
    }
}
