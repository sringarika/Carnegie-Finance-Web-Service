package fund.mymutual.cfsws.rest;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

// {
//   “username”:“Username of the person attempting to login”,
//   “password”: “Password of the person attempting to login”
// }
public class LoginDTO {
    @NotEmpty
    @Length(max = 100)
    private String username;

    @NotEmpty
    @Length(max = 100)
    private String password;

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
}
