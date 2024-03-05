package cn.donting.web.os.core.vo;

/**
 * 消息返回类型枚举
 * @see ResponseBody
 */
public enum ResponseBodyCodeEnum {
    OK(200, "成功"),
    NOT_FOUND(404, "NOT FOUND"),
    UNAUTHORIZED(401 , "Unauthorized"),
    UNKNOWN_ERROR(500, "UNKNOWN ERROR"),
    LOGIN_FAIL(2000, "用户或密码错误"),
    LOGIN_IS_LOGIN(2001, "用户已在其他地方登陆"),
    LOGIN_CREAT_USE(2002, "用户已创建"),
    LOGIN_NONE(2003,401, "未登录"),
    LOGIN_ELSEWHERE(2004, "已在其他地方登陆"),
    USER_OLD_PASSWORD_ERROR(2005, "旧密码错误"),
    WAP_DESKTOP_UNINSTALLED(3000, "未安装桌面"),
    FILE_OPEN_FAIL(2100, "无法打开文件"),
    ;
    /**
     * 状态码
     */
    private final int code;
    /**
     * 消息
     */
    private final String msg;
    private final int httpStatus ;

    ResponseBodyCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.httpStatus = 200;
    }

    ResponseBodyCodeEnum(int code,int httpStatus, String msg) {
        this.code = code;
        this.msg = msg;
        this.httpStatus = httpStatus;
    }
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
