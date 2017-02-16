package fund.mymutual.cfsws.business;

import fund.mymutual.cfsws.model.User;

public interface EmployeeService {
    /**
     * Create a customer.
     * @param customer The customer to create.
     * @throws BusinessLogicException if customer username already exists.
     */
    void createCustomerAccount(User customer) throws BusinessLogicException;

    /**
     * Deposit check for a customer.
     * @param username The customer's username.
     * @param cashInCents The amount to deposit, in cents. (e.g. 123 for $1.23)
     * @throws BusinessLogicException if username does not exist or is not a customer.
     */
    void depositCheck(String username, int cashInCents) throws BusinessLogicException;

    /**
     * Create a fund.
     * @param name The name of the fund to create.
     * @param symbol The symbol of the fund to create.
     * @param initialValueInCents The initial price, in cents. (e.g. 456 for $4.56 per share).
     * @throws BusinessLogicException if symbol already exists.
     */
    void createFund(String name, String symbol, int initialValueInCents) throws BusinessLogicException;

    /**
     * Cause the values of the funds to fluctuate.
     */
    void transitionDay();
}
