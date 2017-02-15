package fund.mymutual.cfsws.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import fund.mymutual.cfsws.model.CustomerPosition;
import fund.mymutual.cfsws.model.Fund;
import fund.mymutual.cfsws.model.JpaUtil;
import fund.mymutual.cfsws.model.User;

public class CustomerServiceImpl implements CustomerService {
    @Override
    public Portfolio getPortfolio(String username) throws BusinessLogicException {
        return JpaUtil.transaction(em -> {
            User user = em.find(User.class, username);
            if (user == null) {
                throw new BusinessLogicException("There is no such user!");
            }
            Set<CustomerPosition> customerpositions = user.getCustomerpositions();
            List<Position> positions = new ArrayList<>(customerpositions.size());

            for (CustomerPosition customerposition : customerpositions) {
                Position position = new Position();
                position.setShares(customerposition.getShares());
                Fund fund = customerposition.getFund();
                position.setName(fund.getFundname());
                position.setPriceInCents(fund.getFundprice());
                positions.add(position);
            }

            Portfolio result = new Portfolio();
            result.setCashInCents(user.getCash());
            result.setPositions(positions);
            return result;
        });
    }

    private Fund fundBySymbol(EntityManager em, String symbol, LockModeType lockModeType) {
        // Fund Symbol is CASE INSENSITIVE!
        TypedQuery<Fund> query = em.createQuery(
                "FROM Fund f WHERE UPPER(f.fundsymbol) = UPPER(:symbol)", Fund.class);
        query.setParameter("symbol", symbol);
        query.setLockMode(lockModeType);
        List<Fund> results = query.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    private CustomerPosition getCustomerPosition(EntityManager em, String username, String symbol,
            LockModeType lockModeType) {
        TypedQuery<CustomerPosition> query = em.createQuery(
                "FROM CustomerPosition p WHERE p.username = :username AND UPPER(p.fund.fundsymbol) = UPPER(:symbol)",
                CustomerPosition.class);
        query.setParameter("username", username);
        query.setParameter("symbol", symbol);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        List<CustomerPosition> positions = query.getResultList();
        if (positions.size() > 0) {
            return positions.get(0);
        }

        return null;
    }

    @Override
    public int buyFund(String username, String symbol, int cashInCents) throws BusinessLogicException {
        return JpaUtil.transaction(em -> {
            User user = em.find(User.class, username, LockModeType.PESSIMISTIC_WRITE);
            if (user == null) {
                throw new BusinessLogicException("There is no such user!");
            }
            Fund fund = fundBySymbol(em, symbol, LockModeType.PESSIMISTIC_WRITE);
            if (fund == null) {
                throw new BusinessLogicException("There is no such fund!");
            }

            if (user.getCash() < cashInCents) {
                // Not enough balance.
                return -1;
            }
            int shares = cashInCents / fund.getFundprice();
            if (shares == 0) {
                // A whole share would cost more than cashInCents, so no shares can be bought.
                return 0;
            }

            int totalCents = shares * fund.getFundprice();
            user.setCash(user.getCash() - totalCents);

            CustomerPosition position = getCustomerPosition(em, username, fund.getFundsymbol(), LockModeType.PESSIMISTIC_WRITE);
            if (position != null) {
                position.setShares(position.getShares() + shares);
            } else {
                position = new CustomerPosition();
                position.setFund(fund);
                position.setUsername(user.getUsername());
                position.setShares(shares);
                em.persist(position);
            }
            return shares;
        });
    }

    @Override
    public boolean sellFund(String username, String symbol, int shares) throws BusinessLogicException {
        return JpaUtil.transaction(em -> {
            User user = em.find(User.class, username, LockModeType.PESSIMISTIC_WRITE);
            if (user == null) {
                throw new BusinessLogicException("There is no such user!");
            }
            Fund fund = fundBySymbol(em, symbol, LockModeType.PESSIMISTIC_WRITE);
            if (fund == null) {
                throw new BusinessLogicException("There is no such fund!");
            }
            CustomerPosition position = getCustomerPosition(em, username, fund.getFundsymbol(), LockModeType.PESSIMISTIC_WRITE);
            if (position == null) {
                return false;
            }
            if (position.getShares() < shares) {
                return false;
            }

            position.setShares(position.getShares() - shares);
            if (position.getShares() == 0) {
                em.remove(position);
            }

            user.setCash(user.getCash() + fund.getFundprice() * shares);

            return true;
        });
    }

    /**
     * Get the position of customer in Fund. For testing only.
     * @param username The username of the customer.
     * @param symbol The symbol of the Fund.
     * @return The CustomerPosition, or null if none.
     */
    CustomerPosition getPosition(String username, String symbol) {
        return JpaUtil.transaction(em -> {
            return getCustomerPosition(em, username, symbol, LockModeType.NONE);
        });
    }

    @Override
    public boolean requestCheck(String username, int cashInCents) throws BusinessLogicException {
        return JpaUtil.transaction(em -> {
            User user = em.find(User.class, username, LockModeType.PESSIMISTIC_WRITE);
            if (user == null) {
                throw new BusinessLogicException("There is no such user!");
            }
            if (user.getCash() < cashInCents) {
                return false;
            }
            user.setCash(user.getCash() - cashInCents);
            return true;
        });
    }

}
