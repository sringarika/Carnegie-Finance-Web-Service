package fund.mymutual.cfsws.rest;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class RequestCheckDTO {
    
    @NotEmpty
    @Length(max=100)
    private String cashValue;
    
    public String getCashValue() {
        return cashValue;
    }
    public void setCashValue(String cashValue) {
        this.cashValue = cashValue;
    }

}
