package cn.donting.web.os.launch;

import cn.donting.web.os.launch.loader.OsClassLoader;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * springboot jar fat 核心加载器，用于加载正式版本核心
 */
public class JarWebosLaunch extends JarLauncher implements WebosLauncher{

    public JarWebosLaunch(File file) throws IOException {
        super(new JarFileArchive(file));
    }
    @Override
    public void launch(String[] args) throws Exception {
        super.launch(args);
    }


    @Override
    protected ClassLoader createClassLoader(URL[] urls) throws Exception {
        OsClassLoader os = new OsClassLoader(urls);
        Thread.currentThread().setContextClassLoader(os);
        return os;
    }

}
