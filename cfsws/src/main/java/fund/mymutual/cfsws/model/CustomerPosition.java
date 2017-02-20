package fund.mymutual.cfsws.model;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="cfs_customerposition")
public class CustomerPosition implements Serializable{
	private static final long serialVersionUID = 1L;
    private int shares; //should be a whole number

    @Id
    private String username;

    @Id
    @ManyToOne
    @JoinColumn(name = "fundsymbol")
    private Fund fund;

    public Fund getFund() {
    	return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public CustomerPosition() {
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