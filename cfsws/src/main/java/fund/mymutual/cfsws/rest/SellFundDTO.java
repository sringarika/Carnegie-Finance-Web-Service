package fund.mymutual.cfsws.rest;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class SellFundDTO {
    
    @NotEmpty
    @Length(max=100)
    private String symbol;
    
    @NotEmpty
    @Length(max=100)
    private String numShares;
    
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getNumShares() {
        return numShares;
    }
    public void setNumShares(String numShares) {
        this.numShares = numShares;
    }
}
