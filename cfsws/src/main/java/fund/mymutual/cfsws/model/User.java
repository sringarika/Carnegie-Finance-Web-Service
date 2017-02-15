package fund.mymutual.cfsws.model;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    private String address;
    private String city;
    private String state;
    private String zip;
    private String email;
    private int cashincents;

    @Enumerated(EnumType.STRING)
    private CFSRole role;

    @OneToMany(mappedBy ="username")   
    Set<CustomerPosition> customerposition;
    
    public Set<CustomerPosition> getCustomerpositions() {
		return customerposition;
    }
    
    public User() {

    }

    /**
     * Gets the username.
     *
     * @return     The username.
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the first name.
     *
     * @return     The first name.
     */

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name.
     *
     * @param      newString  The new string
     *
     * @return     The last name.
     */
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the password hash.
     *
     * @return     The password hash.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * { function_description }
     *
     * @param      password  The password
     *
     * @return     { description_of_the_return_value }
     */
    public boolean verifyPassword(String password) {
        return BCrypt.checkpw(password, this.passwordHash);
    }

    /**
     * { function_description }
     *
     * @param      password  The password
     */
    public void updatePassword(String password) {
        this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Gets the role.
     *
     * @return     The role.
     */
    public CFSRole getRole() {
        return role;
    }

    public void setRole(CFSRole role) {
        this.role = role;
    }
    /**
     * Gets the address.
     */
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * Gets the city.
     */
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state.
     */
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the zip.
     */
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * Gets the email.
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the cash.
     */
    public int getCash() {
        return cashincents;
    }

    public void setCash(int cashincents) {
        this.cashincents = cashincents;
    }
}