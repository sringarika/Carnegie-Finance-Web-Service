package fund.mymutual.cfsws.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cfs_fund")
public class Fund {
    @Id
    private String fundsymbol;
    private String fundname;
    private double fundprice;
    private String funddate;


    public Fund() {
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
    
    public double getFundprice() {
    	return fundprice;
    }
    public void setFundprice(double fundprice) {
    	fundprice = this.fundprice;
    }
    
    public String getFunddate() {
    	return funddate;
    }
    public void setFunddate(String funddate) {
    	funddate = this.funddate;
    }
}