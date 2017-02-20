package fund.mymutual.cfsws.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cfs_fund")
public class Fund {
    private String fundsymbol;
    private String fundname;
    private int fundprice; //in cents
    private String funddate;

    @Id
    private String upperSymbol;

    @Column(unique=true)
    private String upperName;

    public Fund() {
    }

    public String getFundsymbol() {
    	return fundsymbol;
    }
    public void setFundsymbol(String fundsymbol) {
    	this.fundsymbol = fundsymbol;
    	this.upperSymbol = fundsymbol.toUpperCase();
    }

    public String getFundname() {
    	return fundname;
    }
    public void setFundname(String fundname) {
    	this.fundname = fundname;
        this.upperName = fundname.toUpperCase();
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