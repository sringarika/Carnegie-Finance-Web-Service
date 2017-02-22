package fund.mymutual.cfsws.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fund.mymutual.cfsws.business.BusinessLogicException;
import fund.mymutual.cfsws.model.CFSRole;

@ControllerAdvice
public class CFSErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @Order(100)
    @ResponseBody
    public MessageDTO processValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logExceptionInvalid(request, ex);
        return new MessageDTO("The input you provided is not valid");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Order(100)
    public MessageDTO processValidationError(HttpMessageNotReadableException ex, HttpServletRequest request) {
        logException(request, ex);
        return new MessageDTO("The input you provided is not valid");
    }

    @ExceptionHandler(CFSUnauthorizedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Order(100)
    public MessageDTO processUnauthorizedException(CFSUnauthorizedException ex, HttpServletRequest request) {
        return new MessageDTO("You are not currently logged in");
    }

    @ExceptionHandler(CFSForbiddenException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Order(100)
    public MessageDTO processForbiddenException(CFSForbiddenException ex, HttpServletRequest request) {
        if (ex.getRoleRequired().equals(CFSRole.Employee)) {
            return new MessageDTO("You must be an employee to perform this action");
        } else {
            return new MessageDTO("You must be a customer to perform this action");
        }
    }

    @ExceptionHandler(BusinessLogicException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Order(100)
    public MessageDTO processForbiddenException(BusinessLogicException ex, HttpServletRequest request) {
        logException(request, ex);
        return new MessageDTO("The input you provided is not valid");
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Order(100)
    public MessageDTO processNumberFormatException(NumberFormatException ex, HttpServletRequest request) {
        logException(request, ex);
        return new MessageDTO("The input you provided is not valid");
    }

    @ExceptionHandler(ArithmeticException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Order(100)
    public MessageDTO processArithmeticException(ArithmeticException ex, HttpServletRequest request) {
        logException(request, ex);
        return new MessageDTO("The input you provided is not valid");
    }

    /**
     * A catch-all exception handler as a last resort.
     * @param ex The exception.
     * @param request The HTTP request.
     * @return A default message.
     */
    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Order(Ordered.LOWEST_PRECEDENCE)
    public MessageDTO processException(Exception ex, HttpServletRequest request) {
        try {
            System.out.println("Exception when handling " + request.getMethod() + " " + request.getRequestURI() +
                    ": " + ex.getClass().getName());
            String message = ex.getMessage();
            if (message != null) System.out.println(message);
        } catch (Throwable e) {

        }
        return new MessageDTO("The input you provided is not valid");
    }

    private void logExceptionInvalid(HttpServletRequest request, MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        System.out.println("Validation Error for " + request.getMethod() + " " + request.getRequestURI());
        List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError fieldError: fieldErrors) {
            System.out.println("\t" + fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }
    }

    private void logException(HttpServletRequest request, Exception ex) {
        try {
            System.out.println("Exception when handling " + request.getRequestURI() +  ": " + ex.getClass().getName());
            String message = ex.getMessage();
            if (message != null) System.out.println(message);
        } catch (Throwable e) {

        }
    }
}