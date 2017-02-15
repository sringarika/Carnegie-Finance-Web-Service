package fund.mymutual.cfsws.business;

public interface CustomerService {
    /**
     * Get the cash and funds a customer is holding.
     * @param username The username of the customer.
     * @return The portfolio of the customer.
     * @throws BusinessLogicException if username does not exist or is not a customer.
     */
    Portfolio getPortfolio(String username) throws BusinessLogicException;

    /**
     * Buy a fund for a customer.
     * @param username The username of the customer.
     * @param symbol The symbol of the fund to buy.
     * @param cashInCents The max cash amount to pay, in cents. (e.g. 123 for $1.23)
     * @throws BusinessLogicException if username does not exist or is not a customer.
     * @return The number of shares bough, or -1 if cashInCents is more than the balance.
     *          When the price of the fund is more than cashInCents, the result is 0.
     */
    int buyFund(String username, String symbol, int cashInCents) throws BusinessLogicException;

    /**
     * Sell a fund for a customer.
     * @param username The username of the customer.
     * @param symbol The symbol of the fund to sell.
     * @param shares The number of shares to sell.
     * @return True if selling successful, false if not enough shares.
     * @throws BusinessLogicException if username does not exist or is not a customer.
     */
    boolean sellFund(String username, String symbol, int shares) throws BusinessLogicException;

    /**
     * Request check for a customer.
     * @param username The username of the customer.
     * @param cashInCents The cash amount to withdraw, in cents. (e.g. 123 for $1.23)
     * @return True if request successful, false if not enough balance.
     * @throws BusinessLogicException if username does not exist or is not a customer.
     */
    boolean requestCheck(String username, int cashInCents) throws BusinessLogicException;
}
