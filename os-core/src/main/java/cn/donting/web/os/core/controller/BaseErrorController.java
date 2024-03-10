package cn.donting.web.os.core.controller;

import cn.donting.web.os.core.vo.ResponseBody;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class BaseErrorController {

    @org.springframework.web.bind.annotation.ResponseBody
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseBody fileUploadExceptionHandler(HttpServletResponse response, MaxUploadSizeExceededException exception) {
        response.setStatus(500);
        long maxMb = exception.getMaxUploadSize() / 1024 / 1024;
        ResponseBody fail = cn.donting.web.os.core.vo.ResponseBody.fail(500, "超过最大设置的上传文件限制：" + maxMb);
        return fail;
    }

    @org.springframework.web.bind.annotation.ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseBody fileUploadExceptionHandler(HttpServletResponse response, Exception exception) {
        response.setStatus(500);
        ResponseBody fail = cn.donting.web.os.core.vo.ResponseBody.fail(500, exception.getLocalizedMessage());
        return fail;
    }
}
