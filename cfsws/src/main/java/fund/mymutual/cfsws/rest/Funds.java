package fund.mymutual.cfsws.rest;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class Funds {
    @NotEmpty
    @Length(max = 100)
    private String name;
    @NotEmpty
    @Length(max = 100)
    private String shares;
    @NotEmpty
    @Length(max = 100)
    private String price;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getShares() {
        return shares;
    }
    public void setShares(int shares) {
        this.shares = String.valueOf(shares);
    }
    
    public String getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = String.valueOf(price);
    }
}
