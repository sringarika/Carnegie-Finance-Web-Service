package fund.mymutual.cfsws.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cfs_session")
public class Session {
    @Id
    private String sessionid;
    private String username;
    private String expdate;

    public Session() {
    }

    public String getUsername() {
    	return username;
    }
    public void setUsername(String username) {
    	this.username = username;
    }

    public String getSessionid() {
    	return sessionid;
    }
    public void setSessionid(String sessionid) {
    	this.sessionid = sessionid;
    }

    public String getExpdate() {
    	return expdate;
    }
    public void setExpdate(String expdate) {
    	this.expdate = expdate;
    }
}