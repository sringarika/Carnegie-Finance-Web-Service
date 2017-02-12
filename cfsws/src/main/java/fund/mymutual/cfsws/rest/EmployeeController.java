package fund.mymutual.cfsws.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping(value="/transitionDay", method=RequestMethod.POST)
    public MessageDTO transitionDay(@ModelAttribute("username") String username) {
        employeeService.transitionDay();
        
        return new MessageDTO("The fund was prices have been successfully recalculated");
    }
    
    @RequestMapping(value="/depositCheck", method=RequestMethod.POST)
    public MessageDTO depositeCheck(@ModelAttribute("username") String username, @RequestParam("cashInCents") int cashInCents) 
                                    throws BusinessLogicException {
        if (cashInCents <= 0) {
            return new MessageDTO("The input you provided is not valid");
        }
        String cash = String.valueOf(cashInCents);
        if (cash.length() > 2) {
        cash = cash.substring(0, cash.length()-2) + "." + cash.substring(cash.length()-2);
        } else if (cash.length() == 2) {
            cash = "0." + cash;
        } else if (cash.length() == 1) {
            cash = "0.0" + cash;
        }
        int cashInDollar = Integer.parseInt(cash);
        employeeService.depositCheck(username, cashInDollar);
        return new MessageDTO("The check was successfully deposited");
    }

    
    @RequestMapping(value="/createCustomer", method=RequestMethod.POST)
    public MessageDTO createCustomer(@ModelAttribute("username") String userName, 
                                    @RequestParam String fname, String lname, String address, String city, String state,
                                    String zip, String email, int cash, String password){
        if (fname == null || lname == null || address == null || city == null 
            || state == null || zip == null || email == null || password == null) {
            return new MessageDTO("The input you provided is not valid");
        }
        customerDTO.setFname(fname);
        customerDTO.setLname(lname);
        customerDTO.setAddress(address);
        customerDTO.setCity(city);
        customerDTO.setState(state);
        customerDTO.setZip(zip);
        customerDTO.setEmail(email);
        customerDTO.setPassword(password);
        
        return new MessageDTO(fname + " was registered successfully");
        
    }
    
   
}
