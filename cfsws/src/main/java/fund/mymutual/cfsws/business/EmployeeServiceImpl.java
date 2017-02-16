package fund.mymutual.cfsws.business;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import fund.mymutual.cfsws.model.Fund;
import fund.mymutual.cfsws.model.JpaUtil;
import fund.mymutual.cfsws.model.User;

public class EmployeeServiceImpl implements EmployeeService {

    @Override
    public void createCustomerAccount(User customer) throws BusinessLogicException {
        JpaUtil.transaction(em -> {
            User user = em.find(User.class, customer.getUsername(), LockModeType.PESSIMISTIC_WRITE);
            if (user != null) {
                throw new BusinessLogicException("A user with the same username exists.");
            }
            em.persist(customer);
        });
    }

    @Override
    public void depositCheck(String username, int cashInCents) throws BusinessLogicException {
        JpaUtil.transaction(em -> {
            User user = em.find(User.class, username, LockModeType.PESSIMISTIC_WRITE);
            if (user == null) {
                throw new BusinessLogicException("There is no such user!");
            }
            user.setCash(user.getCash() + cashInCents);
        });
    }

    @Override
    public void createFund(String name, String symbol, int initialValueInCents) throws BusinessLogicException {
        JpaUtil.transaction(em -> {
            // Make sure name and symbol are unique. CaSE insenSITIVE!
            TypedQuery<Fund> query = em.createQuery(
                    "FROM Fund f WHERE UPPER(f.fundname) = UPPER(:name) OR UPPER(f.fundsymbol) = UPPER(:symbol)", Fund.class);
            query.setParameter("name", name);
            query.setParameter("symbol", symbol);
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            if (query.getResultList().size() > 0) {
                throw new BusinessLogicException("A fund with the same name/symbol exists.");
            }

            Fund fund = new Fund();
            fund.setFundname(name);
            fund.setFundsymbol(symbol);
            fund.setFundprice(initialValueInCents);
            fund.setFunddate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            em.persist(fund);
        });
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
