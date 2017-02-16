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
    private String initial_value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getInitial_value() {
        return initial_value;
    }

    public void setInitial_value(String initial_value) {
        this.initial_value = initial_value;
    }
    
}
