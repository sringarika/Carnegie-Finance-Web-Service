package fund.mymutual.cfsws.business;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityExistsException;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import fund.mymutual.cfsws.model.Fund;
import fund.mymutual.cfsws.model.JpaUtil;
import fund.mymutual.cfsws.model.User;

public class EmployeeServiceImpl implements EmployeeService {

    @Override
    public void createCustomerAccount(User customer) throws BusinessLogicException {
        try {
            JpaUtil.transaction(em -> {
                em.persist(customer);
            });
        } catch (PersistenceException e) {
            if (!(e instanceof EntityExistsException)) {
                System.out.println("Exception when creating customer: " + e.getClass().getName());
                System.out.println(e.getMessage());
            }
            // Probably there is a conflict with username?
            throw new BusinessLogicException("A user with the same username exists.");
        }
    }

    @Override
    public void depositCheck(String username, int cashInCents) throws BusinessLogicException {
        JpaUtil.transaction(em -> {
            Query query = em.createQuery("UPDATE User SET cashincents = cashincents + :diff WHERE username = :username");
            query.setParameter("username", username);
            query.setParameter("diff", cashInCents);
            int resultCount = query.executeUpdate();
            if (resultCount == 0) {
                throw new BusinessLogicException("There is no such user!");
            }
        });
    }

    @Override
    public void createFund(String name, String symbol, int initialValueInCents) throws BusinessLogicException {
        Fund fund = new Fund();
        fund.setFundname(name);
        fund.setFundsymbol(symbol);
        fund.setFundprice(initialValueInCents);
        fund.setFunddate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));

        try {
            JpaUtil.transaction(em -> {
                em.persist(fund);
            });
        } catch (PersistenceException e) {
            if (!(e instanceof EntityExistsException)) {
                System.out.println("Exception when creating fund: " + e.getClass().getName());
                System.out.println(e.getMessage());
            }
            // Probably there is a conflict for name or symbol?
            throw new BusinessLogicException("A fund with the same name/symbol exists.");
        }
    }

    @Override
    public void transitionDay() {
        JpaUtil.transaction(em -> {
            TypedQuery<Fund> query = em.createQuery("FROM Fund", Fund.class);
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            List<Fund> funds = query.getResultList();
            Random random = new Random();
            for (Fund fund : funds) {
                int oldPrice = fund.getFundprice();
                int minPrice = (int) Math.ceil(oldPrice * 0.9); // -10%
                if (minPrice <= 0) minPrice = 1;
                int maxPrice = (int) Math.floor(oldPrice * 1.1); // +10%
                int newPrice = minPrice;
                if (maxPrice > minPrice) newPrice = minPrice + random.nextInt(maxPrice - minPrice + 1);
                fund.setFundprice(newPrice);
            }
        });
    }
}
