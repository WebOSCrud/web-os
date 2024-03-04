package cn.donting.web.os.core.loader;


import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

/**
 * jar 加载器，jar应该是一个完整 springboot fat 包
 * @see WapLaunchedURLClassLoader
 * @see org.springframework.boot.loader.LaunchedURLClassLoader
 */
public class JarWapLoader extends WapLoader {

    public JarWapLoader(File file) {
        super(file);
    }

    @Override
    protected String getMainClass() {
        try {
            JarFile jarFile = new JarFile(file);
            Manifest manifest = jarFile.getManifest();
            String value = manifest.getMainAttributes().getValue("Start-Class");
            return value;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    protected WapClassLoader creatWapClassLoader(List<URL> wapLaunchURL) throws Exception {
        WapLauncher wapLauncher = new WapLauncher(file, wapLaunchURL);
        WapClassLoader classLoader = wapLauncher.createClassLoader();
        return classLoader;
    }

    /**
     * 使用 JarLauncher 重springboot  fat 包构建 WapClassLoader。直接加载内部jar
     * @see JarLauncher
     * @see WapLaunchedURLClassLoader
     * @see JarFileArchive
     */
    private static class WapLauncher extends JarLauncher {
        private final List<URL> osWapLaunchURL;

        public WapLauncher(File file, List<URL> osWapLaunchURL) throws IOException {
            super(new JarFileArchive(file));
            this.osWapLaunchURL = osWapLaunchURL;
        }

        @Override
        public String getMainClass() throws Exception {
            return super.getMainClass();
        }

        @Override
        protected WapClassLoader createClassLoader(URL[] urls) throws Exception {
            List<URL> collect = Arrays.stream(urls).collect(Collectors.toList());
            collect.addAll(0, osWapLaunchURL);
            urls = collect.toArray(new URL[collect.size()]);
            return new WapLaunchedURLClassLoader(isExploded(), getArchive(), urls);
        }

        @Override
        public Iterator<Archive> getClassPathArchivesIterator() throws Exception {
            return super.getClassPathArchivesIterator();
        }

        public WapClassLoader createClassLoader() throws Exception {
            Iterator<Archive> classPathArchivesIterator = getClassPathArchivesIterator();
            return (WapClassLoader)createClassLoader(classPathArchivesIterator);
        }

    }

}
