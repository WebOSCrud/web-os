package cn.donting.web.os.core.vo;

import lombok.Data;

/**
 * os http 返回结构体
 * @see ResponseBodyCodeEnum
 * @param <T>
 */
@Data
public class ResponseBody<T> {
    /**
     * 状态码
     * 200 表示成功
     */
    private int code;
    /**
     * 消息
     */
    private String msg;
    /**
     * 数据
     */
    private T data;

    public ResponseBody(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseBody(String msg, T data) {
        this.code = 200;
        this.msg = msg;
        this.data = data;
    }

    public ResponseBody(T data) {
        this.code = 200;
        this.data = data;
    }

    public ResponseBody(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public static <T> ResponseBody<T> success() {
        return new ResponseBody<>(ResponseBodyCodeEnum.OK.getCode(), ResponseBodyCodeEnum.OK.getMsg());
    }
    public static <T> ResponseBody<T> success(T data) {
        return new ResponseBody<>(ResponseBodyCodeEnum.OK.getCode(), ResponseBodyCodeEnum.OK.getMsg(), data);
    }

    public static <T> ResponseBody<T> fail(int code, String msg) {
        return new ResponseBody<>(code, msg);
    }
    public static <T> ResponseBody<T> fail(ResponseBodyCodeEnum code, String msg) {
        return new ResponseBody<>(code.getCode(), msg);
    }
    public static <T> ResponseBody<T> fail(ResponseBodyCodeEnum code) {
        return new ResponseBody<>(code.getCode(), code.getMsg());
    }
    public static <T> ResponseBody<T> failData(ResponseBodyCodeEnum code, T data) {
        return new ResponseBody<>(code.getCode(), code.getMsg(),data);
    }
}
