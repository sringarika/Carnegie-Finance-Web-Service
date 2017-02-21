package fund.mymutual.cfsws.model;
import java.util.Set;

import javax.persistence.Column;
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
    @Column(columnDefinition="VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String username;

    private byte[] firstName;
    private byte[] lastName;
    private String passwordHash;
    private byte[] address;
    private byte[] city;
    private byte[] state;
    private byte[] zip;
    private byte[] email;
    private int cashincents;

    @Enumerated(EnumType.STRING)
    private CFSRole role;

    @OneToMany(mappedBy ="username")
    private Set<CustomerPosition> customerposition;

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
        return new String(firstName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName.getBytes();
    }

    /**
     * Gets the last name.
     *
     * @param      newString  The new string
     *
     * @return     The last name.
     */
    public String getLastName() {
        return new String(lastName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.getBytes();
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
        return new String(address);
    }

    public void setAddress(String address) {
        this.address = address.getBytes();
    }
    /**
     * Gets the city.
     */
    public String getCity() {
        return new String(city);
    }

    public void setCity(String city) {
        this.city = city.getBytes();
    }

    /**
     * Gets the state.
     */
    public String getState() {
        return new String(state);
    }

    public void setState(String state) {
        this.state = state.getBytes();
    }

    /**
     * Gets the zip.
     */
    public String getZip() {
        return new String(zip);
    }

    public void setZip(String zip) {
        this.zip = zip.getBytes();
    }

    /**
     * Gets the email.
     */
    public String getEmail() {
        return new String(email);
    }

    public void setEmail(String email) {
        this.email = email.getBytes();
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