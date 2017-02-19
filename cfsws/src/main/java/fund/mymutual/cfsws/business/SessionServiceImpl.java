package fund.mymutual.cfsws.business;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import fund.mymutual.cfsws.model.JpaUtil;
import fund.mymutual.cfsws.model.Session;
import fund.mymutual.cfsws.model.User;

public class SessionServiceImpl implements SessionService {

    public static final long sessionLifeSeconds = 60 * 15;

    private SecureRandom random;

    public SessionServiceImpl() {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            random = new SecureRandom();
        }
    }

    @Override
    public User authenticateUser(String username, String password) {
        User user = JpaUtil.transaction(em -> {
            return em.find(User.class, username);
        });
        if (user == null || !user.verifyPassword(password)) {
            return null;
        }
        return user;
    }

    private String generateToken() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public String beginSession(String username) {
        String sessionId = null;
        while (sessionId == null) {
            sessionId = generateToken();
            Session session = new Session();
            session.setUsername(username);
            session.setSessionid(sessionId);
            long expire = Instant.now().plusSeconds(sessionLifeSeconds).getEpochSecond();
            session.setExpdate(Long.toString(expire));
            try {
                JpaUtil.transaction(em -> {
                    em.persist(session);
                });
            } catch (PersistenceException e) {
                if (!(e instanceof EntityExistsException)) {
                    System.out.println("Exception when creating session: " + e.getClass().getName());
                    System.out.println(e.getMessage());
                }
            }
        }

        return sessionId;
    }

    @Override
    public User refreshSession(String token) {
        return JpaUtil.transaction(em -> {
            Session session = em.find(Session.class, token);
            if (session == null) return null;
            long expire = Long.parseLong(session.getExpdate());
            if (Instant.now().getEpochSecond() >= expire) {
                em.remove(session);
                return null;
            }
            expire = Instant.now().plusSeconds(sessionLifeSeconds).getEpochSecond();

            // Use UPDATE query to avoid errors when the session ID is gone.
            Query query = em.createQuery("UPDATE Session SET expdate = :expdate WHERE sessionid = :sessionid");
            query.setParameter("sessionid", token);
            query.setParameter("expdate", Long.toString(expire));
            query.executeUpdate();

            String username = session.getUsername();
            return em.find(User.class, username);
        });
    }

    @Override
    public boolean terminateSession(String token) {
        return JpaUtil.transaction(em -> {
            Session session = em.find(Session.class, token);
            if (session == null) return false;
            long expire = Long.parseLong(session.getExpdate());
            boolean sessionValid = true;
            if (Instant.now().getEpochSecond() >= expire) {
                sessionValid = false;
            }

            // Use DELETE query to avoid errors when the session ID is gone.
            Query query = em.createQuery("DELETE Session WHERE sessionid = :sessionid");
            query.setParameter("sessionid", token);
            int deletedCount = query.executeUpdate();
            return sessionValid && deletedCount > 0;
        });
    }

    /**
     * Get Session by sessionId. For testing only.
     * @param sessionId The session ID.
     * @return The Session identified by session ID.
     */
    Session getSessionById(String sessionId) {
        return JpaUtil.transaction(em -> (em.find(Session.class, sessionId)));
    }

    /**
     * Set life for session. For testing only.
     * @param sessionId The session ID.
     */
    void setSecondsRemaining(String sessionId, long seconds) {
        JpaUtil.transaction(em -> {
            Session session = em.find(Session.class, sessionId);
            long newExpire = Instant.now().getEpochSecond() + seconds;
            session.setExpdate(Long.toString(newExpire));
        });
    }
}
