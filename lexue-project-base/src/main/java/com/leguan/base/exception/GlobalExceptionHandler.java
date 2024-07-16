package com.leguan.base.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@ControllerAdvice
//@RestControllerAdvice = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {


    //针对自定义异常处理（LexueException）
    @ResponseBody
    @ExceptionHandler(LexueException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(LexueException e) {

        //记录异常
        log.error("系统异常{}", e.getErrMessage(), e);

        //解析出异常信息
        String errMessage = e.getErrMessage();
        System.out.println(errMessage);
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        System.out.println(restErrorResponse.getErrMessage());
        return restErrorResponse;
    }

    //其他异常处理，如数据库连接错误
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {

        //记录异常
        log.error("系统异常{}", e.getMessage(), e);

        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());
        return restErrorResponse;
    }

}
