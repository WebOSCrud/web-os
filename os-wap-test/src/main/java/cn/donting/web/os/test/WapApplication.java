package cn.donting.web.os.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
@Slf4j
public class WapApplication {

    public static void main(String[] args) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = WapApplication.class.getClassLoader();
        log.info("classLoader:{}",classLoader);
        log.info("contextClassLoader:{}",contextClassLoader);
        ConfigurableApplicationContext run = SpringApplication.run(WapApplication.class, args);
        DispatcherServlet bean = run.getBean(DispatcherServlet.class);
        System.out.println(bean);
    }

}
