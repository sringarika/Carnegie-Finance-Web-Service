package fund.mymutual.cfsws.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MiscController {
    public static final String AUTH_COOKIE = "CFSAuthToken";

    @RequestMapping(value="/error", method=RequestMethod.GET)
    public MessageDTO error() {
        return new MessageDTO("The input you provided is not valid");
    }

    @RequestMapping(value="/", method=RequestMethod.GET)
    public MessageDTO welcome() {
        return new MessageDTO("Welcome to the CFS web service!");
    }

    @RequestMapping(value="/greeting", method=RequestMethod.GET)
    public MessageDTO greeting() {
        return new MessageDTO("Hello from CFS!");
    }
}