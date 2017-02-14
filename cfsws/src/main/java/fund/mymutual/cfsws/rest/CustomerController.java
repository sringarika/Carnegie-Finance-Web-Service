package fund.mymutual.cfsws.rest;

import java.math.BigDecimal;
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

import fund.mymutual.cfsws.business.BusinessLogicException;
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
    
    @RequestMapping(value="/logout", method=RequestMethod.POST)
    public MessageDTO logout(@ModelAttribute("username") String username) {
        return new MessageDTO("You have been successfully logged out");
    }
    
    @RequestMapping(value="/viewPortfolio", method = RequestMethod.GET)
    public ViewPortfolioDTO viewPortfolio(@ModelAttribute("username") String username,
                                          @RequestBody Portfolio portfolio) {
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
    
    @RequestMapping(value="/buyFund", method=RequestMethod.POST) 
    public MessageDTO buyFund(@ModelAttribute("username") String username, @RequestBody BuyFundDTO buyFundDTO) {
        BigDecimal bigDecimal = new BigDecimal(buyFundDTO.getCashValue());
        bigDecimal.scaleByPowerOfTen(2);
        int cashValueInCents = bigDecimal.intValueExact();
        boolean result = true;
        try {
            result = customerService.buyFund(username, buyFundDTO.getSymbol(), cashValueInCents);
            if (result == true) {
                return new MessageDTO("The fund has been successfully purchased");
            } else {
                return new MessageDTO("You don’t have enough cash in your account to make this purchase");
            }
        } catch (BusinessLogicException e) {
            //Two kind of Exceptions: Not Logged In or Not customer
            return new MessageDTO("");
        }  
    }
    
    @RequestMapping(value="/sellFund", method=RequestMethod.POST)
    public MessageDTO sellFund(@ModelAttribute("username") String username, @RequestBody SellFundDTO sellFundDTO) {
        int shares = Integer.parseInt(sellFundDTO.getNumShares());
        boolean result = true;
        try {
            result = customerService.sellFund(username, sellFundDTO.getSymbol(), shares);
            if (result == true) {
                return new MessageDTO("The shares have been successfully sold");
            } else {
                return new MessageDTO("You don’t have that many shares in your portfolio");
            }
        } catch (BusinessLogicException e) {
            //Two kind of Exceptions: Not Logged In or Not customer
            return new MessageDTO("");
        }
    }
    
    @RequestMapping(value="/requestCheck", method=RequestMethod.POST)
    public MessageDTO requestCheck(@ModelAttribute("username") String username, 
                                   @RequestBody RequestCheckDTO requestCheckDTO) {
        BigDecimal bigDecimal = new BigDecimal(requestCheckDTO.getCashValue());
        bigDecimal.scaleByPowerOfTen(2);
        int cashInCents = bigDecimal.intValueExact();
        boolean result = true;
        try {
            result = customerService.requestCheck(username, cashInCents);
            if (result == true) {
                return new MessageDTO("The check has been successfully requeste");
            } else {
                return new MessageDTO("You don’t have sufficient funds in your account to cover the requested check");
            }
        } catch (BusinessLogicException e) {
            //Two kind of Exceptions: Not Logged In or Not customer
            return new MessageDTO(" ");
        }
    }
    
}