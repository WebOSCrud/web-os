package cn.donting.web.os.core.exception;

import cn.donting.web.os.core.vo.ResponseBody;

/**
 * ResponseException
 * @see GlobalExceptionHandler
 */
public class ResponseException extends RuntimeException{

    private ResponseBody responseEntity;

    public ResponseException(ResponseBody responseEntity) {
        super(responseEntity.getCode()+"::"+responseEntity.getMsg());
        this.responseEntity = responseEntity;
    }

    public ResponseBody getResponseEntity() {
        return responseEntity;
    }
}
