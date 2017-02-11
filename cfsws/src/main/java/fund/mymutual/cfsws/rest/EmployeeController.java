package fund.mymutual.cfsws.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}