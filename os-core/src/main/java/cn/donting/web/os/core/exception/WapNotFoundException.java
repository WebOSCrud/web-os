package cn.donting.web.os.core.exception;

/**
 * @author donting
 */
public class WapNotFoundException extends WapException{
    public WapNotFoundException(String wapId) {
        super(wapId,"wap "+wapId+"未找到");
    }
}
