package cn.donting.web.os.wap.launch.autoconfigure;

import cn.donting.web.os.api.OsApi;
import cn.donting.web.os.launch.CoreOsApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置类
 *
 * @author donting
 */
@Configuration
@Slf4j
public class Autoconfigure {
    /**
     * 自动向 wap 的spring 容器注入 OsApi
     *
     * @return
     * @see OsApi
     * @see CoreOsApi
     */
    @Bean
    public OsApi osApi() {
        return CoreOsApi.getOsApi();
    }

}
