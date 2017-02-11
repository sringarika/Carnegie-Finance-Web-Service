package fund.mymutual.cfsws.business;

public interface CustomerBL {
    /**
     * Get the cash and funds a customer is holding.
     * @param username The username of the customer.
     * @return The portfolio of the customer.
     * @throws Exception if username does not exist or is not a customer.
     */
    Portfolio getPortfolio(String username);

    /**
     * Buy a fund for a customer.
     * @param username The username of the customer.
     * @param symbol The symbol of the fund to buy.
     * @param cashInCents The max cash amount to pay, in cents. (e.g. 123 for $1.23)
     * @throws Exception if username does not exist or is not a customer.
     * @return True if purchase successful, false if not enough balance.
     */
    boolean buyFund(String username, String symbol, int cashInCents);

    /**
     * Sell a fund for a customer.
     * @param username The username of the customer.
     * @param symbol The symbol of the fund to sell.
     * @param shares The number of shares to sell.
     * @return True if selling successful, false if not enough shares.
     * @throws Exception if username does not exist or is not a customer.
     */
    boolean sellFund(String username, String symbol, int shares);

    /**
     * Request check for a customer.
     * @param username The username of the customer.
     * @param cashInCents The cash amount to withdraw, in cents. (e.g. 123 for $1.23)
     * @return True if request successful, false if not enough balance.
     * @throws Exception if username does not exist or is not a customer.
     */
    boolean requestCheck(String username, int cashInCents);
}
