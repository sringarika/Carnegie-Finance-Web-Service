package fund.mymutual.cfsws.rest;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class CreateFundDTO {
    
    @NotEmpty
    @Length(max = 100)
    private String name;
    
    @NotEmpty
    @Length(max = 100)
    private String symbol;
    
    @NotEmpty
    private String initialValue;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        name = this.name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    public void setFundsymbol(String symbol) {
        symbol = this.symbol;
    }
    
    public String getInitialValue() {
        return initialValue;
    }
    public void setInitial_value(String initialValue) {
        this.initialValue = initialValue;
    }
}
