package fund.mymutual.cfsws.rest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fund.mymutual.cfsws.business.SessionService;
import fund.mymutual.cfsws.model.User;

@RestController
public class SessionController {
    public static final String AUTH_COOKIE = "CFSAuthToken";

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value="/greeting", method=RequestMethod.GET)
    public MessageDTO greeting() {
        return new MessageDTO("Hello from CFS!");
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public MessageDTO login(@Valid @RequestBody LoginDTO login, HttpServletResponse response) {
        User user = sessionService.authenticateUser(login.getUsername(), login.getPassword());
        if (user == null) { // Failure case
            return new MessageDTO("There seems to be an issue with the username/password combination that you entered");
        } else { // Successful login
            String authToken = sessionService.beginSession(user.getUsername());
            Cookie cookie = new Cookie(AUTH_COOKIE, authToken);
            cookie.setMaxAge(60 * 60 * 24);
            // TODO: Make the cookie expire after remaining idle for 15 minutes.
            // This basically means the cookie should be refreshed every time a response is sent.
            response.addCookie(cookie);
            String firstName = user.getFirstName();
            return new MessageDTO("Welcome " + firstName);
        }
    }

    @RequestMapping(value="/logout", method=RequestMethod.POST)
    public MessageDTO logout(@CookieValue(value=AUTH_COOKIE, required=false) String authToken, HttpServletResponse response) {
        boolean authTokenValid = sessionService.terminateSession(authToken);

        Cookie cookie = new Cookie(AUTH_COOKIE, null);
        cookie.setMaxAge(0); // Remove cookie.
        response.addCookie(cookie);

        if (!authTokenValid) { // Failure case
            return new MessageDTO("You are not currently logged in");
        } else { // Successful login
            return new MessageDTO("You have been successfully logged out");
        }
    }
}