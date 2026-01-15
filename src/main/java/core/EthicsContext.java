package core;

import model.AIDecision;
import model.UserData;

/**
 * Encapsulates the context for an ethics evaluation
 * Contains both the AI decision and user data
 */
public class EthicsContext {
    public final AIDecision decision;
    public UserData userData;
    
    public EthicsContext(AIDecision decision, UserData userData) {
        this.decision = decision;
        this.userData = userData;
    }
}
