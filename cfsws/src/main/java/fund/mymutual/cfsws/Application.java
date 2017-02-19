package fund.mymutual.cfsws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fund.mymutual.cfsws.model.CFSRole;
import fund.mymutual.cfsws.model.JpaUtil;
import fund.mymutual.cfsws.model.User;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        if (System.getenv("NO_SEED_DB") == null) {
            seedDB();
        } else {
            verifyDB();
        }
        SpringApplication.run(Application.class, args);
    }

    private static void seedDB() {
        // Add initial user. This is the only required initial user in specification.
        JpaUtil.transaction(em -> {
            User jadmin = em.find(User.class, "jadmin");
            if (jadmin != null) {
                em.remove(jadmin);
            }
            jadmin = new User();
            jadmin.setUsername("jadmin");
            jadmin.setFirstName("Jane");
            jadmin.setLastName("Admin");
            jadmin.updatePassword("admin");
            jadmin.setAddress("123 Main street");
            jadmin.setCity("Pittsburgh");
            jadmin.setState("Pa");
            jadmin.setZip("15143");
            jadmin.setRole(CFSRole.Employee);
            em.persist(jadmin);
        });
    }

    private static void verifyDB() {
        // Add initial user. This is the only required initial user in specification.
        JpaUtil.transaction(em -> {
            User jadmin = em.find(User.class, "jadmin");
            if (jadmin == null) {
                System.err.println("WARNING: Initial user is missing!");
            }
        });
    }
}
