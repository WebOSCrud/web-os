package cn.donting.web.os.core.config;

import cn.donting.web.os.core.api.OsApi;
import cn.donting.web.os.core.db.repository.IUserRepository;
import cn.donting.web.os.core.db.repository.IWapInfoRepository;
import cn.donting.web.os.core.properties.DevOsProperties;
import cn.donting.web.os.core.service.OsService;
import cn.donting.web.os.core.service.UserService;
import cn.donting.web.os.core.service.WapRuntimeService;
import cn.donting.web.os.core.servlet.OsDispatcherServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Servlet 相关配置
 *
 * @see OsDispatcherServlet
 */
@Configuration
public class ServletConfiguration {
    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet(@Autowired(required = false) DevOsProperties devOsProperties,
                                               OsService osService,
                                               IWapInfoRepository wapInfoRepository,
                                               UserService userService,
                                               OsApi osApi,
                                               IUserRepository iUserRepository,
                                               WapRuntimeService wapRuntimeService,
                                               @Value("${devWapId}") String wapId
    ) {
        return new OsDispatcherServlet(devOsProperties, osService, wapInfoRepository, userService, osApi, wapRuntimeService,iUserRepository,wapId);
    }

}
