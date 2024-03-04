package cn.donting.web.os.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cn.donting.web.os.dev")
@Profile("dev")
@Data
public class DevOsProperties {
    public static final String PREFIX="cn.donting.web.os.dev";
    /**
     * dev 开始模式下的 wap 404 page，主动转发到  pageNotFoundForwardHost
     * 转发路径等于 url 访问路径，会带 waoId. 配置 pageNotFoundForwardWap=false 可以不转发WapId 路径
     *
     * pageNotFoundForwardWap=true  http://127.0.0.1:5137/wapID/idnex.html
     * pageNotFoundForwardWap=false  http://127.0.0.1:5137/index.html
     *
     * @see DevOsProperties#pageNotFoundForwardWapId
     *
     * http://127.0.0.1:5137
     */
    private String pageNotFoundForwardHost;
    /**
     * pageNotFoundForwardHost 转发时是否携带 wapId
     * 默认true
     */
    private boolean pageNotFoundForwardWapId;

    public DevOsProperties() {
        this.pageNotFoundForwardWapId = true;
    }
}
