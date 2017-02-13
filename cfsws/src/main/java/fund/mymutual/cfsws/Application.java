package fund.mymutual.cfsws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fund.mymutual.cfsws.model.JpaUtil;
import fund.mymutual.cfsws.model.User;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        seedDB();
        SpringApplication.run(Application.class, args);
    }

    private static void seedDB() {
        // Add initial user. This is the only required initial user in specification.
        JpaUtil.transaction(em -> {
            if (em.find(User.class, "jadmin") != null) return;
            User jadmin = new User();
            jadmin.setUsername("jadmin");
            jadmin.setFirstName("Jane");
            jadmin.setLastName("Admin");
            jadmin.updatePassword("admin");
            // TODO: Set the following fields.
            // Address: 123 Main street
            // City: Pittsburgh
            // State: Pa
            // Zip: 15143

            em.persist(jadmin);
        });
    }
}