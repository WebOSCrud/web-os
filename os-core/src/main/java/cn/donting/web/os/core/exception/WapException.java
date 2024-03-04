package cn.donting.web.os.core.exception;

import lombok.Getter;

/**
 * Wap 异常
 */
@Getter
public class WapException extends Exception{
    private String wapId;

    public WapException(String wapId) {
        this.wapId = wapId;
    }
    public WapException(String wapId,Exception ex) {
        super(ex);
        this.wapId = wapId;
    }
    public WapException(String wapId,String msg) {
        super(msg);
        this.wapId = wapId;
    }
    public WapException(String wapId,String msg,Exception ex) {
        super(msg,ex);
        this.wapId = wapId;
    }
}
