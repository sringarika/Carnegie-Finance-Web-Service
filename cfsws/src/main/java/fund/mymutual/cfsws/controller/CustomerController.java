package fund.mymutual.cfsws.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    @ModelAttribute("customerId")
    public int customerId(HttpServletRequest request) throws CFSUnauthorizedException, CFSForbiddenException {
        // TODO: Get from session.
        Integer userId = null;
        if (userId == null) {
            throw new CFSUnauthorizedException();
        }

        // TODO: Get from DAO.
        CFSRole role = CFSRole.Employee;
        if (!role.equals(CFSRole.Customer)) {
            throw new CFSForbiddenException(CFSRole.Customer);
        }

        return userId;
    }

    @RequestMapping(value="/example-customer", method=RequestMethod.GET)
    public Message exampleEmployee(@ModelAttribute("customerId") int customerId) {
        return new Message("Hello, customer!");
    }
}