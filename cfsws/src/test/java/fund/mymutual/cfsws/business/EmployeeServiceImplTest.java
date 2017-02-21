package fund.mymutual.cfsws.business;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fund.mymutual.cfsws.model.CFSRole;
import fund.mymutual.cfsws.model.Fund;
import fund.mymutual.cfsws.model.JpaUtil;
import fund.mymutual.cfsws.model.User;

public class EmployeeServiceImplTest {
    private EmployeeService employeeService;

    @Before
    public void setUp() throws Exception {
        employeeService = new EmployeeServiceImpl();
        JpaUtil.transaction(em -> {
            em.createQuery("DELETE FROM CustomerPosition").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Fund").executeUpdate();

            User user = new User();
            user.setUsername("example");
            user.updatePassword("secret");
            user.setFirstName("Foo");
            user.setLastName("Bar");
            user.setEmail("foobar@example.com");
            user.setAddress("Foo Bar");
            user.setCity("Foo");
            user.setState("FB");
            user.setZip("12345");
            user.setCash(123);
            user.setRole(CFSRole.Customer);
            em.persist(user);

            Fund fund = new Fund();
            fund.setFundname("example");
            fund.setFundsymbol("example");
            fund.setFundprice(123);
            fund.setFunddate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            em.persist(fund);
        });
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateCustomer() throws BusinessLogicException {
        User user = new User();
        user.setUsername("example1");
        user.updatePassword("secret1");
        user.setFirstName("Foo1");
        user.setLastName("Bar1");
        user.setEmail("foobar1@example.com");
        user.setAddress("Foo Bar1");
        user.setCity("Foo1");
        user.setState("FB1");
        user.setZip("123451");
        user.setCash(1231);
        user.setRole(CFSRole.Customer);
        employeeService.createCustomerAccount(user);
        User result = JpaUtil.transaction(em -> (em.find(User.class, "example1")));
        Assert.assertNotNull(result);
        Assert.assertEquals(user.getFirstName(), result.getFirstName());
    }

    @Test
    public void testCreateCustomerExists() {
        try {
            User user = new User();
            user.setUsername("example");
            user.updatePassword("secret2");
            user.setFirstName("Foo2");
            user.setLastName("Bar2");
            user.setEmail("foobar2@example.com");
            user.setAddress("Foo Bar2");
            user.setCity("Foo2");
            user.setState("FB2");
            user.setZip("123452");
            user.setCash(1232);
            user.setRole(CFSRole.Customer);
            employeeService.createCustomerAccount(user);
            Assert.fail("Create customer should not allow duplicate username");
        } catch (BusinessLogicException e) {
            return;
        }
    }

    @Test
    public void testCreateCustomerCaseSensitiveUsername() throws BusinessLogicException {
        User user = new User();
        user.setUsername("eXAmple");
        user.updatePassword("secret2");
        user.setFirstName("Foo2");
        user.setLastName("Bar2");
        user.setEmail("foobar2@example.com");
        user.setAddress("Foo Bar2");
        user.setCity("Foo2");
        user.setState("FB2");
        user.setZip("123452");
        user.setCash(1232);
        user.setRole(CFSRole.Customer);
        employeeService.createCustomerAccount(user);
        User result = JpaUtil.transaction(em -> (em.find(User.class, "eXAmple")));
        Assert.assertNotNull(result);
        Assert.assertEquals(user.getFirstName(), result.getFirstName());
    }

    @Test
    public void testDepositCheck() throws BusinessLogicException {
        employeeService.depositCheck("example", 456);
        User result = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123 + 456, result.getCash());
    }

    @Test
    public void testDepositCheckZero() throws BusinessLogicException {
        employeeService.depositCheck("example", 0);
        User result = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123, result.getCash());
    }

    @Test
    public void testDepositCheckNoSuchUser() {
        try {
            employeeService.depositCheck("no-such-user", 123);
            Assert.fail("Deploy check should not be successful for non-existing users.");
        } catch (BusinessLogicException e) {
            return;
        }
    }

    @Test
    public void testCreateFund() throws BusinessLogicException {
        employeeService.createFund("example1", "example1", 123);
    }

    @Test
    public void testCreateFundDuplicateName() {
        try {
            employeeService.createFund("examPLe", "example2", 123);
            Assert.fail("Create fund should not allow duplicate name, case insensitive.");
        } catch (BusinessLogicException e) {
            return;
        }
    }


    @Test
    public void testCreateFundDuplicateSymbol() {
        try {
            employeeService.createFund("example3", "eXaMple", 123);
            Assert.fail("Create fund should not allow duplicate symbol, case insensitive.");
        } catch (BusinessLogicException e) {
            return;
        }
    }

    @Test
    public void testTransitionDay() {
        int price = 123;
        for (int i = 0; i < 100; i++) {
            employeeService.transitionDay();
            Fund fund = JpaUtil.transaction(em -> {
                return em.find(Fund.class, "example");
            });
            int newPrice = fund.getFundprice();
            int minPrice = price - (price / 10);
            int maxPrice = price + (price / 10);
            Assert.assertTrue("price should not decrease more than 10%", newPrice >= minPrice);
            Assert.assertTrue("price should not increase more than 10%", newPrice <= maxPrice);
            Assert.assertTrue("price should not reach zero", newPrice > 0);
            price = newPrice;
        }

        // Let's try again starting with 1.
        price = 1;
        JpaUtil.transaction(em -> {
            em.find(Fund.class, "example").setFundprice(1);
        });

        for (int i = 0; i < 100; i++) {
            employeeService.transitionDay();
            Fund fund = JpaUtil.transaction(em -> {
                return em.find(Fund.class, "example");
            });
            int newPrice = fund.getFundprice();
            int minPrice = price - (price / 10);
            int maxPrice = price + (price / 10);
            Assert.assertTrue("price should not decrease more than 10%", newPrice >= minPrice);
            Assert.assertTrue("price should not increase more than 10%", newPrice <= maxPrice);
            Assert.assertTrue("price should not reach zero", newPrice > 0);
            price = newPrice;
        }
    }

}
