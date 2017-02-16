package fund.mymutual.cfsws.rest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fund.mymutual.cfsws.business.SessionService;
import fund.mymutual.cfsws.model.User;

@RestController
public class LoginController {
    public static final String AUTH_COOKIE = "CFSAuthToken";

    @Autowired
    private SessionService sessionService;

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

    /**
     * A catch-all exception handler for each and every failure case for /login.
     * This is needed because requirements say the error message below should be used for all cases.
     * @param ex The exception.
     * @param request The HTTP request.
     * @return A default message.
     */
    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public MessageDTO processValidationError(Exception ex, HttpServletRequest request) {
        try {
            System.out.println("Exception when handling POST /login: " + ex.getClass().getName());
            String message = ex.getMessage();
            if (message != null) System.out.println(message);
        } catch (Throwable e) {

        }
        // It is a requirement that the follow message shall be returned for all failure cases for /login.
        return new MessageDTO("There seems to be an issue with the username/password combination that you entered");
    }
}