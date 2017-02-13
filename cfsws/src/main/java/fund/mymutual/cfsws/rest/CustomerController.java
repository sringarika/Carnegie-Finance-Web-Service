package fund.mymutual.cfsws.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sound.sampled.Port;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fund.mymutual.cfsws.business.CustomerService;
import fund.mymutual.cfsws.business.Portfolio;
import fund.mymutual.cfsws.business.Position;
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
    
    @RequestMapping(value = "/viewPortfolio", method = RequestMethod.GET)
    public ViewPortfolioDTO viewPortfolio(@RequestBody Portfolio portfolio) {
        List<Position> positionList = portfolio.getPositions();
        String cash = String.valueOf(portfolio.getCashInCents());
        if (cash.length() > 2) {
        cash = cash.substring(0, cash.length()-2) + "." + cash.substring(cash.length()-2);
        } else if (cash.length() == 2) {
            cash = "0." + cash;
        } else if (cash.length() == 1) {
            cash = "0.0" + cash;
        }
        int cashInDollar = Integer.parseInt(cash); 
        ViewPortfolioDTO viewPortfolioDTO = new ViewPortfolioDTO();
        viewPortfolioDTO.setCashInDollar(cashInDollar);
        viewPortfolioDTO.setPositions(positionList);
            
        return viewPortfolioDTO;  
    }
}