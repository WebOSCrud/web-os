package cn.donting.web.os.wap.launch.servlet;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * SpringBoot 在Servlet 容器中启动的类.
 * 参考已war 的方式启动容器,不需要加载tomcat 容器
 * 使用自定义的 ServletContext。
 * @see cn.donting.web.os.core.servlet.WapServletContext
 * @author donting
 */
public class WapSpringBootServletInitializer extends SpringBootServletInitializer {

    private Class<?> mainClass;
    private String[] args=new String[0];

    public WapSpringBootServletInitializer(Class<?> mainClass,String... args) {
        this.mainClass = mainClass;
        if(args!=null){
            this.args=args;
        }
    }

    /**
     * 指定 主类 和启动参数
     * @param builder a builder for the application context
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(mainClass);
        builder.properties(args);
        return builder;
    }

    /**
     * 启动 spring 容器
     * @param servletContext servletContext
     * @return
     */
    public WebApplicationContext start(ServletContext servletContext){
        WebApplicationContext rootApplicationContext = this.createRootApplicationContext(servletContext);
        return rootApplicationContext;
    }




}
