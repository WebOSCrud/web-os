package cn.donting.web.os.core.loader;

import cn.donting.web.os.core.exception.WapLoadException;
import cn.donting.web.os.core.wap.Wap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.archive.Archive;

import java.net.URL;
import java.net.URLClassLoader;
@Slf4j
public class WapClassLoader extends URLClassLoader {

    private String wapId;
    private ThreadGroup threadGroup;
    private Wap wap;
    public WapClassLoader(URL[] urls) {
        super(urls, WapClassLoader.class.getClassLoader().getParent());
    }

    public void setWap(Wap wap) throws WapLoadException {
        if(this.wap!=null){
            return;
        }
        if (wap.getWapClassLoader()!=this) {
            throw new WapLoadException(wapId,wap.getLaodFile(),"WapClassLoader 设置的wap的WapClassLoader 不相等");
        }
        this.wap = wap;
    }

    public String getWapId() {
        return wapId;
    }

    public void setWapId(String wapId) {
        if(this.wapId!=null){
            return;
        }
        this.wapId = wapId;
        threadGroup=new ThreadGroup("wap-"+wapId);
    }

    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

    @Override
    protected void finalize()  {
        log.info("WapClassLoader:"+wapId+" 卸载");
    }
}
