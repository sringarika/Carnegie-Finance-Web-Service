package fund.mymutual.cfsws.rest;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

    @ModelAttribute("username")
    public String authenticateRequest(HttpServletRequest request,
            @CookieValue(value=LoginController.AUTH_COOKIE, required=false) String authToken)
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

    @RequestMapping(value="/transitionDay", method=RequestMethod.POST)
    public MessageDTO transitionDay(@ModelAttribute("username") String username) {
        employeeService.transitionDay();

        return new MessageDTO("The fund prices have been successfully recalculated");
    }

    @RequestMapping(value="/depositCheck", method=RequestMethod.POST)
    public MessageDTO depositCheck(@ModelAttribute("username") String username,
                                   @Valid @RequestBody DepositCheckDTO depositCheckDTO) throws BusinessLogicException {

        BigDecimal bigDecimal = new BigDecimal(depositCheckDTO.getCash());
        BigDecimal newCash = bigDecimal.scaleByPowerOfTen(2);
        int cashInCents = newCash.intValueExact();

        if (cashInCents < 0) {
            return new MessageDTO("The input you provided is not valid");
        } else {
            employeeService.depositCheck(depositCheckDTO.getUsername(), cashInCents);
            return new MessageDTO("The check was successfully deposited");
        }
    }


    @RequestMapping(value="/createCustomerAccount", method=RequestMethod.POST)
    public MessageDTO createCustomerAccount(@ModelAttribute("username") String username,
                                            @Valid @RequestBody CustomerDTO customerDTO) throws BusinessLogicException{
        User customer = new User();
        String cashStr = customerDTO.getCash();
        if (cashStr == null || cashStr.length() == 0) {
            cashStr = "0";
        }
        BigDecimal bigDecimal = new BigDecimal(cashStr);
        BigDecimal newCash = bigDecimal.scaleByPowerOfTen(2);
        int cashInCents = newCash.intValueExact();
        if (cashInCents < 0) {
            return new MessageDTO("The input you provided is not valid");
        }

        customer.setFirstName(customerDTO.getFname());
        customer.setLastName(customerDTO.getLname());
        customer.setAddress(customerDTO.getAddress());
        customer.setCash(cashInCents);
        customer.setCity(customerDTO.getCity());
        customer.setEmail(customerDTO.getEmail());
        customer.updatePassword(customerDTO.getPassword());
        customer.setState(customerDTO.getState());
        customer.setUsername(customerDTO.getUsername());
        customer.setZip(customerDTO.getZip());
        customer.setRole(CFSRole.Customer);
        employeeService.createCustomerAccount(customer);
        return new MessageDTO(customerDTO.getFname() + " was registered successfully");

    }

    @RequestMapping(value="/createFund", method=RequestMethod.POST)
    public MessageDTO createFund(@ModelAttribute("username") String username,
                                 @Valid @RequestBody CreateFundDTO createFundDTO) throws BusinessLogicException {

        BigDecimal bigDecimal = new BigDecimal(createFundDTO.getInitial_value());
        BigDecimal newCash = bigDecimal.scaleByPowerOfTen(2);
        int initialValueInCents = newCash.intValueExact();
        if (initialValueInCents <= 0) {
            return new MessageDTO("The input you provided is not valid");
        }
        employeeService.createFund(createFundDTO.getName(), createFundDTO.getSymbol(), initialValueInCents);
        return new MessageDTO("The fund was successfully created");
    }
}
