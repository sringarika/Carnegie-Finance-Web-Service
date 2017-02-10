package fund.mymutual.cfsws.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fund.mymutual.cfsws.databean.UserBean;
import fund.mymutual.cfsws.model.JpaUtil;

@RestController
public class SessionController {
    public static final String AUTH_COOKIE = "CFSAuthToken";

    @RequestMapping(value="/greeting", method=RequestMethod.GET)
    public Message greeting() {
        return new Message("Hello from CFS!");
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public Message login(@Valid @RequestBody LoginPayload login, HttpServletResponse response) {
        UserBean user = JpaUtil.transaction(em -> {
            return em.find(UserBean.class, login.getUsername());
        });
        if (user == null || !user.verifyPassword(login.getPassword())) { // Failure case
            return new Message("There seems to be an issue with the username/password combination that you entered");
        } else { // Successful login
            // TODO: Generate and store the authToken using DAO.
            String authToken = "random";
            Cookie cookie = new Cookie(AUTH_COOKIE, authToken);
            cookie.setMaxAge(60 * 60 * 24);
            // TODO: Make the cookie expire after remaining idle for 15 minutes.
            // This basically means the cookie should be refreshed every time a response is sent.
            response.addCookie(cookie);
            String firstName = user.getFirstName();
            return new Message("Welcome " + firstName);
        }
    }

    @RequestMapping(value="/logout", method=RequestMethod.POST)
    public Message logout(@CookieValue(value=AUTH_COOKIE, required=false) String authToken, HttpServletResponse response) {
        // TODO: Verify authToken in the database.
        boolean authTokenValid = (authToken != null);

        Cookie cookie = new Cookie(AUTH_COOKIE, null);
        cookie.setMaxAge(0); // Remove cookie.
        response.addCookie(cookie);

        if (!authTokenValid) { // Failure case
            return new Message("You are not currently logged in");
        } else { // Successful login
            return new Message("You have been successfully logged out");
        }
    }
}