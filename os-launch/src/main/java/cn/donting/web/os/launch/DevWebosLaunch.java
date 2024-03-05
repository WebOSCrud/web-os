package cn.donting.web.os.launch;

import cn.donting.web.os.launch.loader.OsClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 开发者 核心 加载器\
 * 源码方式启动
 * @author donting
 */
public class DevWebosLaunch implements WebosLauncher{
    @Override
    public void launch(String[] args) throws Exception {
        String dir = System.getProperty("user.dir");
        File file = new File(dir, "web-os-core.wev");
        if(!file.exists()){
            throw new IOException(file.getName()+" not found!");
        }
        List<String> classpath = Files.readAllLines(file.toPath());
        String mainClass=classpath.get(0);
        List<URL> classpathURL=new ArrayList<>();
        for (int i = 1; i < classpath.size(); i++) {
            String classPath = classpath.get(i);
            if (classPath!=null && !classPath.isEmpty()) {
                classpathURL.add(new File(classPath).toURI().toURL());
            }
        }
        OsClassLoader wapClassLoader = new OsClassLoader(classpathURL.toArray(new URL[classpathURL.size()]));
        Thread.currentThread().setContextClassLoader(wapClassLoader);
        Class<?> aClass = wapClassLoader.loadClass(mainClass);
        Method mainMethod = aClass.getMethod("main", String[].class);
        // 设置方法可访问，因为main方法是public的静态方法
        mainMethod.setAccessible(true);
        mainMethod.invoke(null, (Object) args);
    }



}
