package fund.mymutual.cfsws.rest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fund.mymutual.cfsws.business.SessionService;

@RestController
public class LogoutController {
    @Autowired
    private SessionService sessionService;

    @RequestMapping(value="/logout", method=RequestMethod.POST)
    public MessageDTO logout(@CookieValue(value=LoginController.AUTH_COOKIE, required=false) String authToken, HttpServletResponse response) {
        if (authToken == null) {
            return new MessageDTO("You are not currently logged in");
        }
        boolean authTokenValid = sessionService.terminateSession(authToken);

        Cookie cookie = new Cookie(LoginController.AUTH_COOKIE, null);
        cookie.setMaxAge(0); // Remove cookie.
        response.addCookie(cookie);

        if (!authTokenValid) { // Failure case
            return new MessageDTO("You are not currently logged in");
        } else { // Successful login
            return new MessageDTO("You have been successfully logged out");
        }
    }

    /**
     * A catch-all exception handler for each and every failure case for /logout.
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
            System.out.println("Exception when handling " + request.getRequestURI() +  ": " + ex.getClass().getName());
            String message = ex.getMessage();
            if (message != null) System.out.println(message);
        } catch (Throwable e) {

        }
        // It is a requirement that the follow message shall be returned for all failure cases for /logout.
        return new MessageDTO("You are not currently logged in");
    }
}