package fund.mymutual.cfsws.business;

import java.time.Instant;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fund.mymutual.cfsws.model.Session;
import fund.mymutual.cfsws.model.User;

public class SessionServiceImplTest {
    private SessionServiceImpl sessionService;

    @Before
    public void setUp() throws Exception {
        sessionService = new SessionServiceImpl();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBeginSession() {
        String token = sessionService.beginSession("example");
        Assert.assertNotNull(token);
        Assert.assertNotEquals("", token);
        Session session = sessionService.getSessionById(token);
        Assert.assertNotNull(session);
        Assert.assertEquals(token, session.getSessionid());
        Assert.assertEquals("example", session.getUsername());
        long expire = Long.parseLong(session.getExpdate());
        long maxExpire = Instant.now().getEpochSecond() + SessionServiceImpl.sessionLifeSeconds;
        Assert.assertTrue(expire <= maxExpire);
        Assert.assertTrue(expire > maxExpire - 30);
    }

    @Test
    public void testTerminateNonExistentSession() {
        String token = "foo";
        boolean success = sessionService.terminateSession(token);
        Assert.assertFalse(success);
        success = sessionService.terminateSession(token);
        Assert.assertFalse(success);
    }

    @Test
    public void testTerminateExistingSession() {
        String token = sessionService.beginSession("example");
        boolean success = sessionService.terminateSession(token);
        Assert.assertTrue(success);
        Session session = sessionService.getSessionById(token);
        Assert.assertNull(session);
        // Cannot terminate it twice.
        success = sessionService.terminateSession(token);
        Assert.assertFalse(success);
    }

    @Test
    public void testTerminateExpiredSession() {
        String token = sessionService.beginSession("example");
        sessionService.setSecondsRemaining(token, -1);
        boolean success = sessionService.terminateSession(token);
        Assert.assertFalse(success);
        // The Session should be deleted from the database.
        Session session = sessionService.getSessionById(token);
        Assert.assertNull(session);
    }

    @Test
    public void testRefreshNonExistentSession() {
        String token = "example-token";
        User user = sessionService.refreshSession(token);
        Assert.assertNull(user);
        Session session = sessionService.getSessionById(token);
        Assert.assertNull(session);
        user = sessionService.refreshSession(token);
        Assert.assertNull(user);
    }

    @Test
    public void testRefreshExistingSession() {
        String token = sessionService.beginSession("example");
        sessionService.setSecondsRemaining(token, 30);
        sessionService.refreshSession(token);
        Session session = sessionService.getSessionById(token);
        Assert.assertNotNull(session);
        long expire = Long.parseLong(session.getExpdate());
        long maxExpire = Instant.now().getEpochSecond() + SessionServiceImpl.sessionLifeSeconds;
        // The expire time should be refreshed.
        Assert.assertTrue(expire <= maxExpire);
        Assert.assertTrue(expire > maxExpire - 30);

        boolean success = sessionService.terminateSession(token);
        Assert.assertTrue(success);
        // Cannot terminate it twice.
        success = sessionService.terminateSession(token);
        Assert.assertFalse(success);
    }

    @Test
    public void testRefreshExpiredSession() {
        String token = sessionService.beginSession("example");
        sessionService.setSecondsRemaining(token, -1);
        User user = sessionService.refreshSession(token);
        Assert.assertNull(user);
        // The Session should be deleted from the database.
        Session session = sessionService.getSessionById(token);
        Assert.assertNull(session);
    }

    @Test
    public void testOneUserCanHaveMultipleSessions() {
        String token1 = sessionService.beginSession("example");
        // This will not invalidate token1.
        String token2 = sessionService.beginSession("example");
        boolean success1 = sessionService.terminateSession(token1);
        boolean success2 = sessionService.terminateSession(token2);

        Assert.assertTrue(success1);
        Assert.assertTrue(success2);
    }

    @Test
    public void testMultipleSessionForMultipleUsers() {
        String token1 = sessionService.beginSession("example1");
        // This will not invalidate token1 because it's a different user.
        String token2 = sessionService.beginSession("example2");
        boolean success1 = sessionService.terminateSession(token1);
        boolean success2 = sessionService.terminateSession(token2);

        Assert.assertTrue(success1);
        Assert.assertTrue(success2);
    }
}
