//package cn.donting.web.os.wap.launch;
//
//import cn.donting.web.os.wap.launch.servlet.WapServletConfig;
//import cn.donting.web.os.wap.launch.servlet.WapServletContext;
//import org.apache.catalina.Context;
//import org.apache.catalina.startup.Tomcat;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
//import org.springframework.context.ConfigurableApplicationContext;
//
//import javax.servlet.*;
//
///**
// * Wap 的SpringApplication 加载器
// * @see AnnotationConfigServletWebServerApplicationContext
// * @see ServletContext
// * @see WapSpringApplication#createApplicationContext()
// */
//public class WapSpringApplication extends SpringApplication {
//    public WapSpringApplication(Class<?>... primarySources) {
//        super(primarySources);
//    }
//
//    /**
//     * 重新生成 ConfigurableApplicationContext，使用  AnnotationConfigServletWebServerApplicationContext
//     * 初始化 servletContext，servletConfig.
//     * 设置了 servletContext，则web 容器在启动的时候不会去启动 tomcat. http请求则由核心分发到各个Wap
//     *
//     * @return
//     * @see org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext#createWebServer
//     */
//    @Override
//    protected ConfigurableApplicationContext createApplicationContext() {
////     ConfigurableApplicationContext applicationContext = super.createApplicationContext();
//        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext();
//        ServletContext servletContext = new WapServletContext();
//        context.setServletContext(servletContext);
//        context.setServletConfig(new WapServletConfig(servletContext));
//
//        return context;
//    }
//}
