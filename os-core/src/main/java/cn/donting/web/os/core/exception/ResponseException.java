package cn.donting.web.os.core.exception;

import cn.donting.web.os.core.vo.ResponseBody;

/**
 * ResponseException
 * @see GlobalExceptionHandler
 */
public class ResponseException extends RuntimeException{

    private ResponseBody responseEntity;
    private int httpStatus=500;

    public ResponseException(ResponseBody responseEntity) {
        super(responseEntity.getCode()+"::"+responseEntity.getMsg());
        this.responseEntity = responseEntity;
    }
    public ResponseException(ResponseBody responseEntity,int httpStatus) {
        super(responseEntity.getCode()+"::"+responseEntity.getMsg());
        this.responseEntity = responseEntity;
        this.httpStatus = httpStatus;
    }

    public ResponseBody getResponseEntity() {
        return responseEntity;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
