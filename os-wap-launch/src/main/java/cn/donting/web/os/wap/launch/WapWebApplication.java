package cn.donting.web.os.wap.launch;

import cn.donting.web.os.launch.wap.WapWebSpringApplicationLifecycle;
import cn.donting.web.os.wap.launch.servlet.WapSpringBootServletInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.io.IOException;

/**
 * WapWebApplication 生命周期控制
 * 用于启动/停止 Wap 的 Spring 容器
 * @author donting
 */
public class WapWebApplication implements WapWebSpringApplicationLifecycle {


    private WebApplicationContext webApplicationContext;
    private DispatcherServlet dispatcherServlet;

    @Override
    public void start(Class<?> mainClass, String[] args, ServletContext servletContext, ServletConfig servletConfig) throws Exception {
        if (webApplicationContext != null) {
            return;
        }
        WapSpringBootServletInitializer wapSpringApplication = new WapSpringBootServletInitializer(mainClass,args);

        webApplicationContext = wapSpringApplication.start(servletContext);

        dispatcherServlet = webApplicationContext.getBean(DispatcherServlet.class);
        dispatcherServlet.init(servletConfig);
    }


    @Override
    public int stop() {
        return SpringApplication.exit(webApplicationContext);
    }

    @Override
    public void doService(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        dispatcherServlet.service(request,response);
    }

}
