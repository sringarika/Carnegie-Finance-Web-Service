package fund.mymutual.cfsws.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
            @CookieValue(value=LoginController.AUTH_COOKIE, required=false) String authToken)
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

    private String cashFromCents(int cashInCents) {
        String cash = String.valueOf(cashInCents);
        if (cash.length() > 2) {
        cash = cash.substring(0, cash.length()-2) + "." + cash.substring(cash.length()-2);
        } else if (cash.length() == 2) {
            cash = "0." + cash;
        } else if (cash.length() == 1) {
            cash = "0.0" + cash;
        }
        return cash;
    }

    @RequestMapping(value="/viewPortfolio", method = RequestMethod.GET)
    public MessageDTO viewPortfolio(@ModelAttribute("username") String username) throws BusinessLogicException {
        Portfolio portfolio = customerService.getPortfolio(username);

        List<Position> positionList = portfolio.getPositions();
        if (positionList.size() == 0) {
            return new MessageDTO("You don't have any funds in your Portfolio");
        }
        List<PositionDTO> funds = new ArrayList<PositionDTO>();
        for (Position position : positionList) {
            PositionDTO fund = new PositionDTO();
            fund.setName(position.getName());
            fund.setPrice(cashFromCents(position.getPriceInCents()));
            fund.setShares(Integer.toString(position.getShares()));
            funds.add(fund);
        }
        ViewPortfolioDTO viewPortfolioDTO = new ViewPortfolioDTO("The action was successful");
        viewPortfolioDTO.setCash(cashFromCents(portfolio.getCashInCents()));
        viewPortfolioDTO.setFunds(funds);
        viewPortfolioDTO.setMessage("The action was successful");

        return viewPortfolioDTO;
    }

    @RequestMapping(value="/buyFund", method=RequestMethod.POST)
    public MessageDTO buyFund(@ModelAttribute("username") String username, @Valid @RequestBody BuyFundDTO buyFundDTO)
                            throws BusinessLogicException {
        BigDecimal bigDecimal = new BigDecimal(buyFundDTO.getCashValue());
        BigDecimal newCash = bigDecimal.scaleByPowerOfTen(2);
        int cashValueInCents = newCash.intValueExact();
        if (cashValueInCents < 0) {
            return new MessageDTO("The input you provided is not valid");
        }

        int result = customerService.buyFund(username, buyFundDTO.getSymbol(), cashValueInCents);
        if (result > 0) {
            return new MessageDTO("The fund has been successfully purchased");
        } else if (result == 0){
            return new MessageDTO("You didn't provide enough cash to make this purchase");
        } else {
            return new MessageDTO("You don't have enough cash in your account to make this purchase");
        }

    }

    @RequestMapping(value="/sellFund", method=RequestMethod.POST)
    public MessageDTO sellFund(@ModelAttribute("username") String username, @Valid @RequestBody SellFundDTO sellFundDTO)
                               throws BusinessLogicException {
        int shares = Integer.parseInt(sellFundDTO.getNumShares());
        if (shares < 0) {
            return new MessageDTO("The input you provided is not valid");
        }
        boolean result = true;
        result = customerService.sellFund(username, sellFundDTO.getSymbol(), shares);
        if (result == true) {
            return new MessageDTO("The shares have been successfully sold");
        } else {
            return new MessageDTO("You don't have that many shares in your portfolio");
        }
    }

    @RequestMapping(value="/requestCheck", method=RequestMethod.POST)
    public MessageDTO requestCheck(@ModelAttribute("username") String username,
                                   @Valid @RequestBody RequestCheckDTO requestCheckDTO) throws BusinessLogicException {
        BigDecimal bigDecimal = new BigDecimal(requestCheckDTO.getCashValue());
        BigDecimal newCash = bigDecimal.scaleByPowerOfTen(2);
        int cashInCents = newCash.intValueExact();
        boolean result = true;
        if (cashInCents < 0) {
            return new MessageDTO("The input you provided is not valid");
        }

        result = customerService.requestCheck(username, cashInCents);
        if (result == true) {
            return new MessageDTO("The check has been successfully requested");
        } else {
            return new MessageDTO("You don't have sufficient funds in your account to cover the requested check");
        }
    }
}