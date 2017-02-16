package fund.mymutual.cfsws.rest;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class DepositCheckDTO {
    @NotEmpty
    @Length(max = 100)
    private String username;
    
    @NotEmpty
    private String cash;
    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getCash() {
        return cash;
    }
    
    public void setCash(String cash) {
       this.cash = cash;
    }
}
