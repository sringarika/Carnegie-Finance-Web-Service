package fund.mymutual.cfsws.model;

import java.util.List;

import junit.framework.TestCase;

public class JpaTest extends TestCase {

    public void testTransaction() {
        JpaUtil.transaction(em -> {
            assertNotNull(em);
        });
    }

    public void testUser() {
        JpaUtil.transaction(em -> {
            em.createQuery("DELETE FROM CustomerPosition").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();

            User john = new User();
            john.setUsername("john.d");
            john.setFirstName("John");
            john.setLastName("Doe");
            john.setRole(CFSRole.Customer);
            em.persist(john);

            User jane = new User();
            jane.setUsername("jane.d");
            jane.setFirstName("Jane");
            jane.setLastName("Doe");
            jane.setRole(CFSRole.Employee);
            em.persist(jane);

            List<User> result = em.createQuery("FROM User", User.class).getResultList();
            for (User user : result) {
                if (user.getUsername().equals("john.d")) {
                    assertEquals(user.getFirstName(), "John");
                    assertEquals(user.getLastName(), "Doe");
                    assertEquals(user.getRole(), CFSRole.Customer);
                } else if (user.getUsername().equals("jane.d")) {
                    assertEquals(user.getFirstName(), "Jane");
                    assertEquals(user.getLastName(), "Doe");
                    assertEquals(user.getRole(), CFSRole.Employee);
                } else {
                    fail("Found extra users!");
                }
            }
            em.getTransaction().rollback();
        });
    }
}
