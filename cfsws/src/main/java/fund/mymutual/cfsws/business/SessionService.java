package fund.mymutual.cfsws.business;

import fund.mymutual.cfsws.model.User;

public interface SessionService {
    /**
     * Authenticate a user using username and password.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The authenticated user, or null if authentication failure.
     */
    User authenticateUser(String username, String password);

    /**
     * Begin a session for the user.
     * @param username The username logging in
     * @return A token for authentication.
     */
    String beginSession(String username);

    /**
     * Get the user for the session and extend the duration of the session.
     * @param token The authentication token of the session.
     * @return The user holding the session, or null if session is invalid or expired.
     */
    User refreshSession(String token);

    /**
     * Terminate the session, invalidating the authentication token.
     * @param token The authentication token of the session.
     * @return True if the session is terminated. False if session is invalid or expired.
     */
    boolean terminateSession(String token);
}
