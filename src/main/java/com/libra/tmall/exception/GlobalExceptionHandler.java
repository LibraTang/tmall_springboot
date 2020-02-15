package com.libra.tmall.exception;
 
import javax.servlet.http.HttpServletRequest;
 
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常处理，主要是处理在删除父类信息时，因为外键约束的存在，导致违反约束
 * @ControllerAdvice + @ExceptionHandler 搭配用来全局处理异常
 * 处理所有controller层抛出的Exception异常及其子类的异常
 *
 */
@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public String defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        e.printStackTrace();
        Class constraintViolationException = Class.forName("org.hibernate.exception.ConstraintViolationException");
        if(null!=e.getCause()  && constraintViolationException==e.getCause().getClass()) {
            return "违反了约束，多半是外键约束";
        }
        return e.getMessage();
    }
 
}