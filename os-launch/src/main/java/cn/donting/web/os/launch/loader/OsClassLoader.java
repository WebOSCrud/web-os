package cn.donting.web.os.launch.loader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * WapClassLoader
 * os core 核心加类载器,它没有什么特别之处
 * @author donting
 */
public class OsClassLoader extends URLClassLoader {

    public OsClassLoader(URL[] urls) {
        super(urls);
    }

    @Override
    public String toString() {
        return "OsClassLoader";
    }
}
