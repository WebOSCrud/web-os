package cn.donting.web.os.core.loader;

import cn.donting.web.os.api.wap.WapInfo;
import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.util.FileUtil;
import cn.donting.web.os.core.wap.Wap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * wap 加载器
 * 从文件加载 wap,根据文件类型 选择不同的加载器
 * @see WapLoader#getWapLoader(File)
 * @see JarWapLoader
 * @see DevWapLoader
 */
@Slf4j
public abstract class WapLoader {
    /**
     *
     */
    protected final File file;

    /**
     *
     * @param file 加载的文件
     */
    protected WapLoader(File file) {
        this.file = file;
    }

    /**
     * 获取 os-wap-launch URL。 os-wap-launch会注入到每一个 Wap 的类加载中
     * os-wap-launch 包含 对springboot 以 wap 方式启动的逻辑
     * @return
     * @throws IOException
     */
    private List<URL> getWapLaunchURL() throws IOException {
        List<URL> urls = new ArrayList<>();
        File file = new File("./web-os-wap-launch.wev");
        if (file.exists()) {
            List<String> paths = Files.readAllLines(file.toPath());
            for (String path : paths) {
                urls.add(new File(path).toURI().toURL());
            }
            return urls;
        } else {
            String userDir = System.getProperty("user.dir");
            for (File wapLaunchFile : new File(userDir).listFiles()) {
                if (wapLaunchFile.getName().startsWith("web-os-wap-launch") && wapLaunchFile.getName().endsWith(".jar")) {
                    urls.add(wapLaunchFile.toURI().toURL());
                    return urls;
                }
            }
            throw new RuntimeException("在未找到 "+userDir+" 未找到 web-os-wap-launch*.jar");
        }
    }

    /**
     * 加载为 Wap
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public final Wap load() throws  Exception {
        log.info("loader wap:{}",file);
        List<URL> wapLaunchURL = getWapLaunchURL();
        WapClassLoader classLoader = creatWapClassLoader(wapLaunchURL);
        String mainClassName = getMainClass();
        Class<?> mainClass = classLoader.loadClass(mainClassName);
        try {
            Wap wap = new Wap(file,classLoader, mainClass);
            classLoader.setWap(wap);
            checkWapInfo(wap.getWapInfo());
            return wap;
        }catch (Exception ex){
            throw new IOException(file.getPath()+" "+ex.getMessage(),ex);
        }
    }

    /**
     * 获取main 函数
     * @return
     */
    protected abstract String getMainClass();

    /**
     * 校验 WapInfo 信息
     * @param wapInfo
     * @throws IOException
     */
    private void checkWapInfo(WapInfo wapInfo) throws IOException {
        if(wapInfo.getId()==null && !StringUtils.hasText(wapInfo.getId())){
            throw new IOException("wap.info.json 缺失 id");
        }
    }

    /**
     * 创建 WapClassLoader
     * @see WapLaunchedURLClassLoader
     * @param wapLaunchURL
     * @return
     * @throws Exception
     */
    protected abstract WapClassLoader creatWapClassLoader(List<URL> wapLaunchURL) throws Exception;

    /**
     * 更具文件类型 jar,classpath.dev 获取不同的加载器
     * @param file wap 文件
     * @return
     */
    public static WapLoader getWapLoader(File file) {
        String extName = FileUtil.extName(file);
        if (extName.equals(OsCoreApplication.WPA_DEV_EXT_NAME)) {
            return new DevWapLoader(file);
        }
        return new JarWapLoader(file);
    }
}
