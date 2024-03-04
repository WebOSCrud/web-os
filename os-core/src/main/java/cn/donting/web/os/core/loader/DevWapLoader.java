package cn.donting.web.os.core.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * classpath.dev 加载器
 *
 * @author dongting
 */
public class DevWapLoader extends WapLoader {
    DevWapLoader(File file) {
        super(file);
    }

    @Override
    protected String getMainClass() {
        try {
            return Files.readAllLines(file.toPath()).get(0).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<URL> getURLS() throws IOException {
        List<URL> urls = new ArrayList<>();
        List<String> classPath = Files.readAllLines(file.toPath());
        for (int i = 1; i < classPath.size(); i++) {
            urls.add(new File(classPath.get(i).trim()).toURI().toURL());
        }

        return urls;
    }

    @Override
    protected WapClassLoader creatWapClassLoader(List<URL> wapLaunchURL) throws IOException {
        List<URL> urls = getURLS();
        urls.addAll(0,wapLaunchURL);
        return new WapClassLoader(urls.toArray(new URL[urls.size()]));
    }
}
