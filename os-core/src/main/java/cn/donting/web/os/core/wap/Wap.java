package cn.donting.web.os.core.wap;

import cn.donting.web.os.core.db.entity.WapInfo;
import cn.donting.web.os.core.loader.WapClassLoader;
import cn.donting.web.os.core.servlet.WapServletContext;
import cn.donting.web.os.launch.wap.WapWebSpringApplicationLifecycle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * wap 实例。
 * 需要注意的 的是 wap 可能内部 会有自定义的 线程在运行，因此 只是停止 spring 容器，并不能完全 结束wap.WapClassLoader 并不会被回收
 * 只有当  WapClassLoader 被GC回收时，wap才算真正从jvm 卸载
 * <p>
 * 使用一个 ThreadGroup 来作为 一个 wap 的 ThreadGroup，用于监控 wap内部创建的线程
 * </p>
 * <p>
 * 用于在 停止容器是没有自动结束 wap 内部创建的线程，就可以通过 ThreadGroup 来获取创建的线程来强制结束。使jvm 能够正确的卸载 classloader,
 * 但是需要注意该方法强制结束线程不能确保 数据安全。
 * </p>
 *
 * @see Thread#group
 * @see Thread#Thread(ThreadGroup, String)
 * @see Thread#Thread(String)
 * @see ThreadGroup
 * @see SecurityManager
 * @see cn.donting.web.os.core.service.WapRuntimeService
 */
@Slf4j
public class Wap {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private WapInfo wapInfo;
    /**
     * wap 类加载器
     */
    @Getter
    @JsonIgnore
    private final WapClassLoader wapClassLoader;
    /**
     * 主类
     */
    private Class<?> mainClass;
    /**
     * wap 对应的springboot 容器
     */
    private WapWebSpringApplicationLifecycle wapWebSpringApplication;
    /**
     * http 最近一次访问当前Wap 的时间
     * 用于 长时间不访问自动 结束wap
     */
    @Getter
    @Setter
    private long httpLastVisitTimme;
    /**
     * 启动时间
     */
    @Getter
    private long startTime;
    /**
     * 启动耗时
     */
    @Getter
    private long startUpTime;
    /**
     * 状态
     */
    @Getter
    private WapStatus wapStatus;
    /**
     * 加载的文件
     */
    @Getter
    @JsonIgnore
    private final File laodFile;

    public Wap(File file, WapClassLoader wapClassLoader, Class<?> mainClass) throws IOException {
        wapStatus = WapStatus.Loading;
        laodFile = file;
        this.wapClassLoader = wapClassLoader;
        this.mainClass = mainClass;
        httpLastVisitTimme = System.currentTimeMillis();
        URL resource = wapClassLoader.getResource("wap.info.json");
        if (resource == null) {
            throw new IOException("不是一个 Wap，缺少 wap.info.json");
        }
        try {
            wapInfo = objectMapper.readValue(resource, WapInfo.class);
            wapClassLoader.setWapId(wapInfo.getId());
        } catch (Exception ex) {
            throw new IOException("wap.info.json 加载失败，请检查wap.info.json", ex);
        }
        wapStatus = WapStatus.Loaded;
    }

    /**
     * 启动wap 的 spring 容器
     * @param args
     * @param servletContext
     * @param servletConfig
     * @throws Exception
     */
    public void start(String[] args, ServletContext servletContext, ServletConfig servletConfig) throws Exception {
        wapStatus = WapStatus.Starting;
        startTime = System.currentTimeMillis();
        log.info("start Wap:{}, args:{}", wapInfo.getId(), Arrays.asList(args));
        Class<?> aClass = wapClassLoader.loadClass("cn.donting.web.os.wap.launch.WapWebApplication");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(wapClassLoader);
        try {
            wapWebSpringApplication = (WapWebSpringApplicationLifecycle) aClass.newInstance();
            wapWebSpringApplication.start(mainClass, args, servletContext, servletConfig);
            startUpTime = System.currentTimeMillis() - startTime;
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
        wapStatus = WapStatus.Running;
    }

    /**
     * 停止spring 容器。
     * 并中断 wap 线程组中的所有线程
     *
     * @throws Exception 停止时异常
     */
    public void stop() throws Exception {
        wapStatus = WapStatus.Stopping;
        wapWebSpringApplication.stop();
        ThreadGroup threadGroup = wapClassLoader.getThreadGroup();
        int count = threadGroup.activeCount();
        log.info(threadGroup.getName() + "activeCount:{}", count);
        //终止线程组
        threadGroup.interrupt();
        wapStatus = WapStatus.Stopped;
    }

    /**
     * 执行http 请求
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @see org.springframework.web.servlet.DispatcherServlet
     * @see org.springframework.web.servlet.DispatcherServlet#doService(HttpServletRequest, HttpServletResponse)
     */
    public void doService(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        wapWebSpringApplication.doService(request, response);
        httpLastVisitTimme = System.currentTimeMillis();
    }

    @Override
    protected void finalize() {
        log.info("Wap卸载：{}", wapInfo.getId());
    }
}
