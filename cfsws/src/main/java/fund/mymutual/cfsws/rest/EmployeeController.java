package fund.mymutual.cfsws.rest;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fund.mymutual.cfsws.business.BusinessLogicException;
import fund.mymutual.cfsws.business.EmployeeService;
import fund.mymutual.cfsws.business.SessionService;
import fund.mymutual.cfsws.model.CFSRole;
import fund.mymutual.cfsws.model.User;

@RestController
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private CustomerDTO customerDTO;

    @ModelAttribute("username")
    public String authenticateRequest(HttpServletRequest request,
            @CookieValue(value=SessionController.AUTH_COOKIE, required=false) String authToken)
            throws CFSUnauthorizedException, CFSForbiddenException {
        if (authToken == null) {
            throw new CFSUnauthorizedException();
        }

        User user = sessionService.refreshSession(authToken);
        if (user == null) {
            throw new CFSUnauthorizedException();
        }

        if (!user.getRole().equals(CFSRole.Employee)) {
            throw new CFSForbiddenException(CFSRole.Employee);
        }
        return user.getUsername();
    }

    @RequestMapping(value="/example-employee", method=RequestMethod.GET)
    public MessageDTO exampleEmployee(@ModelAttribute("username") String username) {
        return new MessageDTO("Hello, " + username + "!");
    }
    
    @RequestMapping(value="/logout", method=RequestMethod.POST)
    public MessageDTO logout(@ModelAttribute("username") String username) {
        return new MessageDTO("You have been successfully logged out");
    }

    @RequestMapping(value="/transitionDay", method=RequestMethod.POST)
    public MessageDTO transitionDay(@ModelAttribute("username") String username) {
        employeeService.transitionDay();
        
        return new MessageDTO("The fund was prices have been successfully recalculated");
    }
    
    @RequestMapping(value="/depositCheck", method=RequestMethod.POST)
    public MessageDTO depositCheck(@ModelAttribute("username") String username, @RequestBody DepositCheckDTO depositCheckDTO) {
        if (Integer.valueOf(depositCheckDTO.getCash()) <= 0) {
            return new MessageDTO("The input you provided is not valid");
        }
        BigDecimal bigDecimal = new BigDecimal(depositCheckDTO.getCash());
        bigDecimal.scaleByPowerOfTen(2);
        int cashInCents = bigDecimal.intValueExact();
            try {
                employeeService.depositCheck(depositCheckDTO.getUsername(), cashInCents);
            } catch (NumberFormatException | BusinessLogicException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return new MessageDTO("The check was successfully deposited");
    }

    
    @RequestMapping(value="/createCustomer", method=RequestMethod.POST)
    public MessageDTO createCustomer(@ModelAttribute("username") String userName, 
                                    @RequestBody CustomerDTO customerDTO){
        User customer = new User();
        BigDecimal bigDecimal = new BigDecimal(customerDTO.getCash());
        bigDecimal.scaleByPowerOfTen(2);
        int cashInCents = bigDecimal.intValueExact();
        customer.setCash(cashInCents);
        
        try {
            employeeService.createCustomer(customer);
        } catch (BusinessLogicException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new MessageDTO(customerDTO.getFname() + " was registered successfully");
        
    }
    
    @RequestMapping(value="/creatFund", method=RequestMethod.POST)
    public MessageDTO createFund(@ModelAttribute("username") String userName,
                                    @RequestBody CreateFundDTO createFundDTO) {
        
        BigDecimal bigDecimal = new BigDecimal(createFundDTO.getInitialValue());
        bigDecimal.scaleByPowerOfTen(2);
        int initialValueInCents = bigDecimal.intValueExact();
        try {
            employeeService.createFund(createFundDTO.getName(), createFundDTO.getSymbol(), initialValueInCents);
        } catch (BusinessLogicException e) {
            return new MessageDTO("");
        }
        return new MessageDTO("");
    }
}
