package fund.mymutual.cfsws.business;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fund.mymutual.cfsws.model.CFSRole;
import fund.mymutual.cfsws.model.CustomerPosition;
import fund.mymutual.cfsws.model.Fund;
import fund.mymutual.cfsws.model.JpaUtil;
import fund.mymutual.cfsws.model.User;

public class CustomerServiceImplTest {
    private CustomerServiceImpl customerService;

    @Before
    public void setUp() throws Exception {
        customerService = new CustomerServiceImpl();

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

            Fund fund1 = new Fund();
            fund1.setFundsymbol("fund1");
            fund1.setFundname("Fund 1");
            fund1.setFundprice(123);
            fund1.setFunddate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            em.persist(fund1);

            Fund fund2 = new Fund();
            fund2.setFundsymbol("fund2");
            fund2.setFundname("Fund 2");
            fund2.setFundprice(123);
            fund2.setFunddate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            em.persist(fund2);

            CustomerPosition position2 = new CustomerPosition();
            position2.setUsername("example");
            position2.setFund(fund2);
            position2.setShares(123);
            em.persist(position2);
        });
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetPortfolio() throws BusinessLogicException {
        Portfolio portfolio = customerService.getPortfolio("example");
        Assert.assertNotNull(portfolio);
        Assert.assertEquals(123, portfolio.getCashInCents());
        List<Position> positions = portfolio.getPositions();
        Assert.assertNotNull(positions);
        Assert.assertEquals(1, positions.size());
        Position position = positions.get(0);
        Assert.assertEquals(123, position.getShares());
        Assert.assertEquals("Fund 2", position.getName());
        Assert.assertEquals(123, position.getPriceInCents());
    }

    @Test
    public void testGetPortfolioWithZeroShares() throws BusinessLogicException {
        JpaUtil.transaction(em -> {
            Fund fund1 = em.find(Fund.class, "fund1");
            CustomerPosition position1 = new CustomerPosition();
            position1.setUsername("example");
            position1.setFund(fund1);
            position1.setShares(0);
            em.persist(position1);
        });
        Portfolio portfolio = customerService.getPortfolio("example");
        Assert.assertNotNull(portfolio);
        Assert.assertEquals(123, portfolio.getCashInCents());
        List<Position> positions = portfolio.getPositions();
        Assert.assertNotNull(positions);
        Assert.assertEquals(1, positions.size());
        Position position = positions.get(0);
        Assert.assertEquals(123, position.getShares());
        Assert.assertEquals("Fund 2", position.getName());
        Assert.assertEquals(123, position.getPriceInCents());
    }

    @Test
    public void testBuyFund() throws BusinessLogicException {
        int shares = customerService.buyFund("example", "fund1", 123);
        Assert.assertEquals(1, shares);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(0, user.getCash());
        CustomerPosition position = customerService.getPosition("example", "fund1");
        Assert.assertEquals(1, position.getShares());
    }

    @Test
    public void testBuyFundSymbolIgnoreCase() throws BusinessLogicException {
        int shares = customerService.buyFund("example", "fUNd1", 123);
        Assert.assertEquals(1, shares);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(0, user.getCash());
        CustomerPosition position = customerService.getPosition("example", "fund1");
        Assert.assertEquals(1, position.getShares());
    }

    @Test
    public void testBuyFundExistingPosition() throws BusinessLogicException {
        JpaUtil.transaction(em -> {
            em.find(User.class, "example").setCash(456);
        });
        int shares = customerService.buyFund("example", "fund2", 123);
        Assert.assertEquals(1, shares);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(456 - 123, user.getCash());
        CustomerPosition position = customerService.getPosition("example", "fund2");
        Assert.assertEquals(124, position.getShares());
    }

    @Test
    public void testBuyFundNotEnoughCash() throws BusinessLogicException {
        int shares = customerService.buyFund("example", "fund1", 456);
        Assert.assertEquals(-1, shares);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123, user.getCash());
    }

    @Test
    public void testBuyFundZeroShare() throws BusinessLogicException {
        int shares = customerService.buyFund("example", "fund1", 12);
        Assert.assertEquals(0, shares);
    }

    @Test
    public void testBuyFundZeroCash() throws BusinessLogicException {
        int shares = customerService.buyFund("example", "fund1", 0);
        Assert.assertEquals(0, shares);
    }

    @Test
    public void testBuyFundNoSuchUser() {
        try {
            customerService.buyFund("no-such-user", "fund1", 123);
            Assert.fail("Buy fund should not succeed with no such user.");
        } catch (BusinessLogicException e) {
            return;
        }
    }

    @Test
    public void testBuyFundNoSuchFund() {
        try {
            customerService.buyFund("example", "no-such-fund", 123);
            Assert.fail("Buy fund should not succeed with non-existent fund.");
        } catch (BusinessLogicException e) {
            return;
        }
    }

    @Test
    public void testSellFund() throws BusinessLogicException {
        boolean result = customerService.sellFund("example", "fund2", 123);
        Assert.assertTrue(result);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123 + 123 * 123, user.getCash());
        CustomerPosition position = customerService.getPosition("example", "fund2");
        Assert.assertNull(position);
    }

    @Test
    public void testSellFundSymbolIgnoreCase() throws BusinessLogicException {
        boolean result = customerService.sellFund("example", "FunD2", 123);
        Assert.assertTrue(result);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123 + 123 * 123, user.getCash());
        CustomerPosition position = customerService.getPosition("example", "fund2");
        Assert.assertNull(position);
    }

    @Test
    public void testSellFundRemaining() throws BusinessLogicException {
        boolean result = customerService.sellFund("example", "fund2", 79);
        Assert.assertTrue(result);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123 + 79 * 123, user.getCash());
        CustomerPosition position = customerService.getPosition("example", "fund2");
        Assert.assertEquals(123 - 79, position.getShares());
    }

    @Test
    public void testSellFundZero() throws BusinessLogicException {
        boolean result = customerService.sellFund("example", "fund2", 0);
        Assert.assertTrue(result);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123, user.getCash());
        CustomerPosition position = customerService.getPosition("example", "fund2");
        Assert.assertEquals(123, position.getShares());
    }

    @Test
    public void testSellFundNotEnoughShares() throws BusinessLogicException {
        boolean result = customerService.sellFund("example", "fund2", 456);
        Assert.assertFalse(result);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123, user.getCash());
        CustomerPosition position = customerService.getPosition("example", "fund2");
        Assert.assertEquals(123, position.getShares());
    }

    @Test
    public void testSellFundNoPosition() throws BusinessLogicException {
        boolean result = customerService.sellFund("example", "fund1", 123);
        Assert.assertFalse(result);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123, user.getCash());
        CustomerPosition position = customerService.getPosition("example", "fund1");
        Assert.assertNull(position);
    }

    @Test
    public void testSellFundNoPositionZero() throws BusinessLogicException {
        boolean result = customerService.sellFund("example", "fund1", 0);
        Assert.assertTrue(result); // Pending requirement clarification.
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123, user.getCash());
        CustomerPosition position = customerService.getPosition("example", "fund1");
        Assert.assertNull(position);
    }

    @Test
    public void testSellFundNoSuchUser() {
        try {
            customerService.sellFund("no-such-user", "example", 123);
            Assert.fail("Sell fund should not succeed with no such user.");
        } catch (BusinessLogicException e) {
            return;
        }
    }

    @Test
    public void testSellFundNoSuchFund() {
        try {
            customerService.sellFund("example", "no-such-fund", 123);
            Assert.fail("Sell fund should not succeed with non-existent fund.");
        } catch (BusinessLogicException e) {
            return;
        }
    }

    @Test
    public void testRequestCheck() throws BusinessLogicException {
        boolean result = customerService.requestCheck("example", 123);
        Assert.assertTrue(result);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(0, user.getCash());
    }

    @Test
    public void testRequestCheckZero() throws BusinessLogicException {
        boolean result = customerService.requestCheck("example", 0);
        Assert.assertTrue(result);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123, user.getCash());
    }

    @Test
    public void testRequestCheckNoSuchUser() {
        try {
            customerService.requestCheck("no-such-user", 123);
            Assert.fail("Cannot request check with no such user.");
        } catch (BusinessLogicException e) {
            return;
        }
    }

    @Test
    public void testRequestCheckNotEnoughCash() throws BusinessLogicException {
        boolean result = customerService.requestCheck("example", 456);
        Assert.assertFalse(result);
        User user = JpaUtil.transaction(em -> (em.find(User.class, "example")));
        Assert.assertEquals(123, user.getCash());
    }
}
