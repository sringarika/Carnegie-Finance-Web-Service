package fund.mymutual.cfsws.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fund.mymutual.cfsws.databean.CFSRole;
import fund.mymutual.cfsws.databean.UserBean;

@RestController
public class EmployeeController {
    @ModelAttribute("username")
    public String authenticateRequest(HttpServletRequest request,
            @CookieValue(value=SessionController.AUTH_COOKIE, required=false) String authToken)
            throws CFSUnauthorizedException, CFSForbiddenException {
        if (authToken == null) {
            throw new CFSUnauthorizedException();
        }
        // TODO: Get by authToken using DAO
        UserBean user = new UserBean();
        user.setUsername("jadmin");
        user.setRole(CFSRole.Employee);
        if (user == null) {
            throw new CFSUnauthorizedException();
        }

        if (!user.getRole().equals(CFSRole.Employee)) {
            throw new CFSForbiddenException(CFSRole.Employee);
        }
        return user.getUsername();
    }

    @RequestMapping(value="/example-employee", method=RequestMethod.GET)
    public Message exampleEmployee(@ModelAttribute("username") String username) {
        return new Message("Hello, " + username + "!");
    }
}