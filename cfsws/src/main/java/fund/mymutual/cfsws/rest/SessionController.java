package fund.mymutual.cfsws.rest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fund.mymutual.cfsws.business.SessionService;

@RestController
public class SessionController {
    public static final String AUTH_COOKIE = "CFSAuthToken";

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value="/greeting", method=RequestMethod.GET)
    public MessageDTO greeting() {
        return new MessageDTO("Hello from CFS!");
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