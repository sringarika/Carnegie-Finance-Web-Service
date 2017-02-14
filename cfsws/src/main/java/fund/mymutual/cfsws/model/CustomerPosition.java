package fund.mymutual.cfsws.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cfs_customerposition")
public class CustomerPosition {
    @Id
    private String fundsymbol;
    private String fundname;
    private int shares; //should be a whole number
    @Id
    private String username;

    public CustomerPosition() {
    }
    
    public String getFundsymbol() {
    	return fundsymbol;
    }
    public void setFundsymbol(String fundsymbol) {
    	this.fundsymbol = fundsymbol;
    }
    
    public String getFundname() {
    	return fundname;
    }
    public void setFundname(String fundname) {
    	this.fundname = fundname;
    }
    
    public int getShares() {
    	return shares;
    }
    public void setShares(int shares) {
    	this.shares = shares;
    }
    
    public String getUsername() {
    	return username;
    }
    public void setUsername(String username) {
    	this.username = username;
    }
}