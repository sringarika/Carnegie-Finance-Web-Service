package fund.mymutual.cfsws.model;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cfs_fund")
public class Fund {
    @Id
    private String fundsymbol;
    private String fundname;
    private int fundprice; //in cents
    private String funddate;
    
    
    public Fund() {
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
    
    public int getFundprice() {
    	return fundprice;
    }
    public void setFundprice(int fundprice) {
    	this.fundprice = fundprice;
    }
    
    public String getFunddate() {
    	return funddate;
    }
    public void setFunddate(String funddate) {
    	this.funddate = funddate;
    }
}