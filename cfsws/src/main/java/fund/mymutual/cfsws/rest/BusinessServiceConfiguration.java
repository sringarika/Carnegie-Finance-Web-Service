package fund.mymutual.cfsws.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fund.mymutual.cfsws.business.CustomerService;
import fund.mymutual.cfsws.business.CustomerServiceImpl;
import fund.mymutual.cfsws.business.EmployeeService;
import fund.mymutual.cfsws.business.EmployeeServiceImpl;
import fund.mymutual.cfsws.business.SessionService;
import fund.mymutual.cfsws.business.SessionServiceImpl;

@Configuration
public class BusinessServiceConfiguration {
    @Bean
    public SessionService sessionService() {
        return new SessionServiceImpl();
    }

    @Bean EmployeeService employeeService() {
        return new EmployeeServiceImpl();
    }

    @Bean CustomerService customerService() {
        return new CustomerServiceImpl();
    }
}