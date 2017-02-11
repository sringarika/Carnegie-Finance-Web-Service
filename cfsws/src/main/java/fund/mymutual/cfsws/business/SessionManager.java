package fund.mymutual.cfsws.business;

import fund.mymutual.cfsws.databean.UserBean;

public interface SessionManager {
    /**
     * Begin a session for the user.
     * @param username The username logging in
     * @return A token for authentication.
     */
    String beginSession(String username);

    /**
     * Get the user for the session.
     * @param token The authentication token of the session.
     * @return The user holding the session, or null if session is invalid or expired.
     */
    UserBean getSession(String token);

    /**
     * Terminate the session, invalidating the authentication token.
     * @param token The authentication token of the session.
     * @return True if the session is terminated. False if session is invalid or expired.
     */
    boolean terminateSession(String token);
}
