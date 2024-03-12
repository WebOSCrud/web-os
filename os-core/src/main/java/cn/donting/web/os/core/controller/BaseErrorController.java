package cn.donting.web.os.core.controller;

import cn.donting.web.os.core.exception.ResponseException;
import cn.donting.web.os.core.vo.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletResponse;
@Slf4j
@ControllerAdvice
public class BaseErrorController {


    @ExceptionHandler(value = ResponseException.class)
    public ResponseBody responseBodyException(HttpServletResponse response, ResponseException exception) {
        response.setStatus(exception.getHttpStatus());
        ResponseBody fail = exception.getResponseEntity();
        return fail;
    }

    @org.springframework.web.bind.annotation.ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseBody fileUploadExceptionHandler(HttpServletResponse response, Exception exception) {
        log.error(exception.getMessage(), exception);
        response.setStatus(500);
        ResponseBody fail = cn.donting.web.os.core.vo.ResponseBody.fail(500, exception.getLocalizedMessage());
        return fail;
    }
}
