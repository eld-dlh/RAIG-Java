package core;

import model.AIDecision;
import model.UserData;

public class EthicsContext {
    public AIDecision decision;
    public UserData userData;

    public EthicsContext(AIDecision decision, UserData userData) {
        this.decision = decision;
        this.userData = userData;
    }
}
