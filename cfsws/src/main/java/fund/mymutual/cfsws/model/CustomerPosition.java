package fund.mymutual.cfsws.model;
import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="cfs_customerposition")
public class CustomerPosition implements Serializable{
    @Id
    private String fundsymbol;
    private int shares; //should be a whole number
    @Id
    private String username;
    
    
    @ManyToOne
    @JoinColumn(name = "fundsymbol")
    private Fund fund;
    
    public Fund getFund() {
    	return fund;
    }
    
    public CustomerPosition() {
    }
    
    public String getFundsymbol() {
    	return fundsymbol;
    }
    public void setFundsymbol(String fundsymbol) {
    	this.fundsymbol = fundsymbol;
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