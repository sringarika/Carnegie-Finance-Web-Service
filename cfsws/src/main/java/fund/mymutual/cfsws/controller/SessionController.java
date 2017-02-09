package fund.mymutual.cfsws.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
    @RequestMapping(value="/greeting", method=RequestMethod.GET)
    public Message greeting() {
        return new Message("Hello from CFS!");
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public Message login(@Valid @RequestBody LoginPayload login) {
        // TODO: Use DAO to login.
        if (!login.getPassword().equals("secret")) { // Failure case
            return new Message("There seems to be an issue with the username/password combination that you entered");
        } else { // Successful login
            // TODO: Set session.
            // TODO: Get first name by username.
            String firstName = "Alice";
            return new Message("Welcome " + firstName);
        }
    }

    @RequestMapping(value="/logout", method=RequestMethod.POST)
    public Message logout() {
        // TODO: Get session.
        boolean hasSession = false;
        if (!hasSession) { // Failure case
            return new Message("You are not currently logged in");
        } else { // Successful login
            return new Message("You have been successfully logged out");
        }
    }
}