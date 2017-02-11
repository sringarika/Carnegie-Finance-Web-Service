package fund.mymutual.cfsws.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fund.mymutual.cfsws.business.CustomerService;
import fund.mymutual.cfsws.business.EmployeeService;
import fund.mymutual.cfsws.business.Portfolio;
import fund.mymutual.cfsws.business.Position;
import fund.mymutual.cfsws.business.SessionService;
import fund.mymutual.cfsws.model.CFSRole;
import fund.mymutual.cfsws.model.JpaUtil;
import fund.mymutual.cfsws.model.User;

@Configuration
public class BusinessServiceConfiguration {
    @Bean
    public SessionService sessionService() {
        // TODO: Use a real implementation of SessionService.
        return new SessionService() {
            @Override
            public User authenticateUser(String username, String password) {
                User user = JpaUtil.transaction(em -> {
                    return em.find(User.class, username);
                });
                if (user == null || !user.verifyPassword(password)) {
                    return null;
                }
                return user;
            }

            @Override
            public String beginSession(String username) {
                String authToken = "random";
                return authToken;
            }

            @Override
            public User refreshSession(String token) {
                if (token.equals("random")) {
                    User user = new User();
                    user.setUsername("jadmin");
                    user.setRole(CFSRole.Employee);
                    return user;
                } else {
                    return null;
                }
            }

            @Override
            public boolean terminateSession(String token) {
                return false;
            }
        };
    }

    @Bean EmployeeService employeeService() {
        // TODO: Use a real implementation of EmployeeService.
        return new EmployeeService() {
            @Override
            public void createCustomer(User customer) {
                // TODO Auto-generated method stub

            }

            @Override
            public void depositCheck(String username, int cashInCents) {
                // TODO Auto-generated method stub

            }

            @Override
            public void createFund(String name, String symbol, int initialValueInCents) {
                // TODO Auto-generated method stub

            }

            @Override
            public void transitionDay() {
                // TODO Auto-generated method stub

            }
        };
    }

    @Bean CustomerService customerService() {
        // TODO: Use a real implementation of CustomerService.
        return new CustomerService() {
            @Override
            public Portfolio getPortfolio(String username) {
                List<Position> positions = new ArrayList<>();

                Position p1 = new Position();
                p1.setName("fund1");
                p1.setPriceInCents(678);
                p1.setShares(901);
                positions.add(p1);

                Position p2 = new Position();
                p2.setName("fund2");
                p2.setPriceInCents(200);
                p2.setShares(300);
                positions.add(p2);

                Portfolio portfolio = new Portfolio();
                portfolio.setCashInCents(12345);
                portfolio.setPositions(positions);
                return portfolio;
            }

            @Override
            public boolean buyFund(String username, String symbol, int cashInCents) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean sellFund(String username, String symbol, int shares) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean requestCheck(String username, int cashInCents) {
                // TODO Auto-generated method stub
                return false;
            }
        };
    }
}