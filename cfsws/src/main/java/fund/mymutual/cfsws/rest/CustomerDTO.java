package fund.mymutual.cfsws.rest;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class CustomerDTO {

    @NotEmpty
    @Length(max = 100)
    private String username;

    @NotEmpty
    @Length(max = 100)
    private String password;

    @NotEmpty
    @Length(max = 100)
    private String fname;

    @NotEmpty
    @Length(max = 100)
    private String lname;

    @NotEmpty
    @Length(max = 100)
    private String address;

    @NotEmpty
    @Length(max = 100)
    private String city;

    @NotEmpty
    @Length(max = 100)
    private String state;

    @NotEmpty
    @Length(max = 100)
    private String zip;

    @NotEmpty
    @Length(max = 100)
    private String email;

    private String cash;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }
    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }
    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCash() {
        return cash;
    }
    public void setCash(String cash) {
        this.cash = cash;
    }
}
