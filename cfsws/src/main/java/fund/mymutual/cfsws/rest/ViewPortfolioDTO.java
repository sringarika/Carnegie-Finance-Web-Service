package fund.mymutual.cfsws.rest;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import fund.mymutual.cfsws.business.Position;

public class ViewPortfolioDTO extends MessageDTO {
    @NotEmpty
    private String message;
    
    @NotEmpty
    private String cash;
    
    @NotNull
    private List<Funds> funds;
    
    public ViewPortfolioDTO(String message) {
        super(message);
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public List<Funds> getFunds() {
        return funds;
    }

    public void setFunds(List<Funds> funds) {
        this.funds = funds;
    }
}
