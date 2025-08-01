package kr.hhplus.be.server.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {
    
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleBindException(BindException e) {
        return ApiResponse.fail(
            HttpStatus.BAD_REQUEST.value(), 
            e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleException(Exception e) {
        return ApiResponse.fail(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "서버 내부 오류가 발생했습니다."
        );
    }
}
