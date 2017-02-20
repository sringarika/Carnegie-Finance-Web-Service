package fund.mymutual.cfsws.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
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
                if (customerposition.getShares() == 0) continue;
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

    private Fund fundBySymbol(EntityManager em, String symbol) {
        return em.find(Fund.class, symbol.toUpperCase());
    }

    private CustomerPosition getCustomerPosition(EntityManager em, String username, String symbol,
            LockModeType lockModeType) {
        TypedQuery<CustomerPosition> query = em.createQuery(
                "FROM CustomerPosition p WHERE p.username = :username AND p.fund.upperSymbol = UPPER(:symbol)",
                CustomerPosition.class);
        query.setParameter("username", username);
        query.setParameter("symbol", symbol);
        query.setLockMode(lockModeType);
        List<CustomerPosition> positions = query.getResultList();
        CustomerPosition position = null;
        if (positions.size() > 0) {
            position = positions.get(0);
            if (position.getShares() == 0) {
                return null;
            }
        }

        return position;
    }

    @Override
    public int buyFund(String username, String symbol, int cashInCents) throws BusinessLogicException {
        Fund fund = JpaUtil.transaction(em -> {
            User user = em.find(User.class, username);
            if (user == null) {
                throw new BusinessLogicException("There is no such user!");
            }
            Fund fund1 = fundBySymbol(em, symbol);
            if (fund1 == null) {
                throw new BusinessLogicException("There is no such fund!");
            }
            return fund1;
        });
        if (cashInCents == 0) {
            return 0;
        }
        int shares = cashInCents / fund.getFundprice();
        if (shares == 0) {
            // A whole share would cost more than cashInCents, so no shares can be bought.
            return 0;
        }

        CustomerPosition position = new CustomerPosition();
        position.setFund(fund);
        position.setUsername(username);
        position.setShares(0);
        try {
            JpaUtil.transaction(em -> {
                em.persist(position);
            });
        } catch (PersistenceException e) {
            if (!(e instanceof EntityExistsException)) {
                System.out.println("Exception when creating CustomerPosition: " + e.getClass().getName());
                System.out.println(e.getMessage());
            }
            // Probably the CustomerPosition exists.
        }

        return JpaUtil.transaction(em -> {
            Query query = em.createQuery("UPDATE User SET cashincents = cashincents + :diff WHERE username = :username AND cashincents >= -:diff");
            query.setParameter("username", username);
            query.setParameter("diff", -cashInCents);
            int resultCount = query.executeUpdate();
            if (resultCount == 0) {
                return -1;
            }

            Query query2 = em.createQuery("UPDATE CustomerPosition SET shares = shares + :buying WHERE username = :username AND fund.upperSymbol = :symbol");
            query2.setParameter("username", username);
            query2.setParameter("symbol", symbol.toUpperCase());
            query2.setParameter("buying", shares);
            int resultCount2 = query2.executeUpdate();
            if (resultCount2 != 1) {
                throw new BusinessLogicException("Update failed for CustomerPosition");
            }

            return shares;
        });
    }

    @Override
    public boolean sellFund(String username, String symbol, int shares) throws BusinessLogicException {
        JpaUtil.transaction(em -> {
            User user = em.find(User.class, username);
            if (user == null) {
                throw new BusinessLogicException("There is no such user!");
            }
            Fund fund = fundBySymbol(em, symbol);
            if (fund == null) {
                throw new BusinessLogicException("There is no such fund!");
            }
        });
        if (shares == 0) {
            return true;
        }
        return JpaUtil.transaction(em -> {
            Query query = em.createQuery("UPDATE CustomerPosition SET shares = shares - :selling WHERE username = :username AND fund.upperSymbol = :symbol AND shares >= :selling");
            query.setParameter("username", username);
            query.setParameter("symbol", symbol.toUpperCase());
            query.setParameter("selling", shares);
            int resultCount = query.executeUpdate();
            if (resultCount == 0) {
                // Either no such position or not enough shares.
                return false;
            }

            Fund fund = fundBySymbol(em, symbol);
            int cashInCents = fund.getFundprice() * shares;
            Query query2 = em.createQuery("UPDATE User SET cashincents = cashincents + :diff WHERE username = :username");
            query2.setParameter("username", username);
            query2.setParameter("diff", cashInCents);
            int resultCount2 = query2.executeUpdate();
            if (resultCount2 == 0) {
                throw new BusinessLogicException("There is no such user!");
            }
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
        JpaUtil.transaction(em -> {
            User user = em.find(User.class, username);
            if (user == null) {
                throw new BusinessLogicException("There is no such user!");
            }
        });
        return JpaUtil.transaction(em -> {
            Query query = em.createQuery("UPDATE User SET cashincents = cashincents + :diff WHERE username = :username AND cashincents >= -:diff");
            query.setParameter("username", username);
            query.setParameter("diff", -cashInCents);
            int resultCount = query.executeUpdate();
            if (resultCount == 0) {
                return false;
            }
            return true;
        });
    }

}
