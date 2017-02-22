package fund.mymutual.cfsws.rest;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

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

    @Bean
    public Filter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(5120);
        return filter;
    }

    @Bean
    public Filter logResponseBodyFilter() {
        return new ResponseBodyLoggingFilter();
    }
}