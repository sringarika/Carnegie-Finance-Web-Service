package fund.mymutual.cfsws.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name="cfs_user")
public class User {
    @Id
    private String username;
    private String firstName;
    private String lastName;
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private CFSRole role;

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean verifyPassword(String password) {
        return BCrypt.checkpw(password, this.passwordHash);
    }

    public void updatePassword(String password) {
        this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public CFSRole getRole() {
        return role;
    }

    public void setRole(CFSRole role) {
        this.role = role;
    }
}
