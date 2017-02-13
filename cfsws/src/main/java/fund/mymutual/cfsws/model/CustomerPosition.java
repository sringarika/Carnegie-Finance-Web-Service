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
    	fundsymbol = this.fundsymbol;
    }
    
    public String getFundname() {
    	return fundname;
    }
    public void setFundname(String fundname) {
    	fundname = this.fundname;
    }
    
    public double getShares() {
    	return shares;
    }
    public void setShares(int shares) {
    	shares = this.shares;
    }
    
    public String getUsername() {
    	return username;
    }
    public void setUsername(String username) {
    	username = this.username;
    }
}