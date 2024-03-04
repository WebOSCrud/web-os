package cn.donting.web.os.launch.wap;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.ApplicationContext;

import javax.servlet.*;
import java.io.IOException;

/**
 * Wap Application
 * 控制wap生命周期
 * @author donting
 */
public interface WapWebSpringApplicationLifecycle {
    /**
     * 启动
     *
     * @param mainClass 主类
     * @param args 启动参数
     * @throws Exception
     */
    void start(Class<?> mainClass, String[] args, ServletContext servletContext, ServletConfig servletConfig) throws Exception;

    /**
     * 停止
     * @see org.springframework.boot.SpringApplication#exit(ApplicationContext, ExitCodeGenerator...)
     * @return 退出码
     */
    int stop() throws Exception;

    /**
     * http请求 分发
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @see org.springframework.web.servlet.DispatcherServlet
     */
    void doService(ServletRequest request, ServletResponse response) throws ServletException, IOException;
}
