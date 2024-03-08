package cn.donting.web.os.core.config;

import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.file.OSFileSpaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.function.Predicate;

/**
 * Web mvc 配置
 */
@Configuration
public class WapWebMvcConfigurer implements WebMvcConfigurer {

    public static final String USER_AVATAR_PATH = "/" + OsCoreApplication.OS_ID + "/user/avatar";
    public static final String WAP_RESOURCES_PATH = "/" + OsCoreApplication.OS_ID + "/os/wap-resources";

    @Autowired
    WebProperties webProperties;

    /**
     * 设置 静态资源 。添加前缀 wapId
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ///用户头像
        registry.addResourceHandler(USER_AVATAR_PATH + "/**").addResourceLocations("file:" + OSFileSpaces.OS_USER_AVATAR.getPath() + "/");
        registry.addResourceHandler(WAP_RESOURCES_PATH + "/**").addResourceLocations("file:" + OSFileSpaces.OS_WAP_RESOURCES.getPath() + "/");
        //主程序 修改静态资源
        if (webProperties.getResources().getStaticLocations() != null) {
            String[] staticLocations = webProperties.getResources().getStaticLocations();
            //注册 设置的 staticLocations
            registry.addResourceHandler("/" + OsCoreApplication.OS_ID + "/**").addResourceLocations(staticLocations);
        }

    }


    /**
     * 增加统一前缀 os
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(OsCoreApplication.OS_ID, aClass -> true);
    }

}
