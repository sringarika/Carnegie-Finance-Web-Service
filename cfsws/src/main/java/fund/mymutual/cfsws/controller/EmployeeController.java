package fund.mymutual.cfsws.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    @ModelAttribute("employeeId")
    public int employeeId(HttpServletRequest request) throws CFSUnauthorizedException, CFSForbiddenException {
        // TODO: Get from session.
        Integer userId = 1;
        if (userId == null) {
            throw new CFSUnauthorizedException();
        }

        // TODO: Get from DAO.
        CFSRole role = CFSRole.Customer;
        if (!role.equals(CFSRole.Employee)) {
            throw new CFSForbiddenException(CFSRole.Employee);
        }
        return userId;
    }

    @RequestMapping(value="/example-employee", method=RequestMethod.GET)
    public Message exampleEmployee(@ModelAttribute("employeeId") int employeeId) {
        return new Message("Hello, employee!");
    }
}