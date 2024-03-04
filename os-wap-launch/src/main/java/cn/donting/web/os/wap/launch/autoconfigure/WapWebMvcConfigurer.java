package cn.donting.web.os.wap.launch.autoconfigure;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Method;

/**
 * WebMvc 配置
 * @author donting
 */
@Configuration
public class WapWebMvcConfigurer implements WebMvcConfigurer {

    private final WebProperties webProperties;

    public WapWebMvcConfigurer(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    /**
     * 给 Controller 添加统一前缀 WapId
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(getWapId(), aClass -> true);
    }

    /**
     * 设置 静态资源 。添加前缀 wapId
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String wapId = getWapId();
        if (webProperties.getResources().getStaticLocations() != null) {
            String[] staticLocations = webProperties.getResources().getStaticLocations();
            //注册 设置的 staticLocations
            registry.addResourceHandler("/" + wapId + "/**").addResourceLocations(staticLocations);
        }

    }

    /**
     * wap 由 WapClassloader 加载.通过反射可以获取 WapId
     * @return
     */
    private String getWapId() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Method getWapIdMethod = contextClassLoader.getClass().getMethod("getWapId");
            String wapId = (String) getWapIdMethod.invoke(contextClassLoader);
            return wapId;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
