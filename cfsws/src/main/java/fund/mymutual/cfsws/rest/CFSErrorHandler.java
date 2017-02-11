package fund.mymutual.cfsws.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fund.mymutual.cfsws.model.CFSRole;

@ControllerAdvice
public class CFSErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageDTO processValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logException(request, ex);
        return new MessageDTO("The input you provided is not valid");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageDTO processValidationError(HttpMessageNotReadableException ex, HttpServletRequest request) {
        logException(request, ex);
        return new MessageDTO("The input you provided is not valid");
    }

    @ExceptionHandler(CFSUnauthorizedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageDTO processUnauthorizedException(CFSUnauthorizedException ex, HttpServletRequest request) {
        logException(request, ex);
        return new MessageDTO("You are not currently logged in");
    }

    @ExceptionHandler(CFSForbiddenException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageDTO processForbiddenException(CFSForbiddenException ex, HttpServletRequest request) {
        logException(request, ex);
        if (ex.getRoleRequired().equals(CFSRole.Employee)) {
            return new MessageDTO("You must be an employee to perform this action");
        } else {
            // TODO: There's a spelling error (an -> a) in the Specification. Please verify with client.
            return new MessageDTO("You must be an customer to perform this action");
        }
    }

    private void logException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        System.out.println("Validation Error for " + request.getMethod() + " " + request.getRequestURI());
        List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError fieldError: fieldErrors) {
            System.out.println("\t" + fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }
    }

    private void logException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        System.out.println("Invalid JSON for " + request.getMethod() + " " + request.getRequestURI());
    }

    private void logException(HttpServletRequest request, CFSUnauthorizedException ex) {
        System.out.println("User not logged in for " + request.getMethod() + " " + request.getRequestURI());
    }

    private void logException(HttpServletRequest request, CFSForbiddenException ex) {
        System.out.println("User forbidden for " + request.getMethod() + " " + request.getRequestURI());
    }
}