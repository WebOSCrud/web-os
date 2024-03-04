package cn.donting.web.os.core.exception;

import cn.donting.web.os.core.servlet.OsDispatcherServlet;
import lombok.Getter;

import java.io.File;

/**
 * wap 加载异常
 * @see OsDispatcherServlet
 * @see cn.donting.web.os.core.service.WapRuntimeService#getAndLoadWap(String)
 */
@Getter
public class WapLoadException extends WapException{

    private File file;
    public WapLoadException(String wapId,File file,Exception ex) {
        super(wapId,file.getPath()+":"+ex.getMessage(),ex);
        this.file=file;
    }
    public WapLoadException(String wapId,File file,String msg) {
        super(wapId,msg);
        this.file=file;
    }
    public WapLoadException(String wapId,File file,String msg,Exception ex) {
        super(wapId,file.getPath()+":"+msg,ex);
        this.file=file;
    }
}
