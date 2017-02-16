package fund.mymutual.cfsws.rest;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class BuyFundDTO {
    
    @NotEmpty
    @Length(max=100)
    private String symbol;
    
    @NotEmpty
    @Length(max=100)
    private String cashValue;
    
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getCashValue() {
        return cashValue;
    }
    
    public void setCashValue(String cashValue) {
        this.cashValue = cashValue;
    }
}
