package cn.donting.web.os.core.exception;

import cn.donting.web.os.core.vo.ResponseBody;
import cn.donting.web.os.core.vo.ResponseBodyCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 * @author donting
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseBody handleGlobalException(Exception ex) {
        if (ex instanceof ResponseException) {
            log.debug(((ResponseException) ex).getResponseEntity().getMsg(), ex);
            return ((ResponseException) ex).getResponseEntity();
        }
        log.error(ex.getMessage(), ex);
        ResponseBody<Object> fail = ResponseBody.fail(ResponseBodyCodeEnum.UNKNOWN_ERROR.getCode(), ex.getClass().getName() +"："+ ex.getMessage());
        return fail;
    }

}
