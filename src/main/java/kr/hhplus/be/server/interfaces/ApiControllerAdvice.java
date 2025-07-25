package kr.hhplus.be.server.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleException(BindException e) {
        return ApiResponse.fail(
            HttpStatus.BAD_REQUEST.value(), 
            e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
    }
}
