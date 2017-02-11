package fund.mymutual.cfsws.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fund.mymutual.cfsws.business.CustomerService;
import fund.mymutual.cfsws.business.SessionService;
import fund.mymutual.cfsws.model.CFSRole;
import fund.mymutual.cfsws.model.User;

@RestController
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private SessionService sessionService;

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

        if (!user.getRole().equals(CFSRole.Customer)) {
            throw new CFSForbiddenException(CFSRole.Customer);
        }
        return user.getUsername();
    }

    @RequestMapping(value="/example-customer", method=RequestMethod.GET)
    public MessageDTO exampleCustomer(@ModelAttribute("username") String username) {
        return new MessageDTO("Hello, " + username + "!");
    }
}