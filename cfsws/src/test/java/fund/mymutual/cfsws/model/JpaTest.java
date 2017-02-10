package fund.mymutual.cfsws.model;

import java.util.List;

import fund.mymutual.cfsws.databean.CFSRole;
import fund.mymutual.cfsws.databean.UserBean;
import junit.framework.TestCase;

public class JpaTest extends TestCase {

    public void testTransaction() {
        JpaUtil.transaction(em -> {
            assertNotNull(em);
        });
    }

    public void testUserBean() {
        JpaUtil.transaction(em -> {
            em.createQuery("DELETE FROM UserBean").executeUpdate();

            UserBean john = new UserBean();
            john.setUsername("john.d");
            john.setFirstName("John");
            john.setLastName("Doe");
            john.setRole(CFSRole.Customer);
            em.persist(john);

            UserBean jane = new UserBean();
            jane.setUsername("jane.d");
            jane.setFirstName("Jane");
            jane.setLastName("Doe");
            jane.setRole(CFSRole.Employee);
            em.persist(jane);

            List<UserBean> result = em.createQuery("FROM UserBean", UserBean.class).getResultList();
            for (UserBean user : result) {
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
