package cn.donting.web.os.core.config;

import cn.donting.web.os.core.api.OsApi;
import cn.donting.web.os.core.db.repository.IOsFileTypeRepository;
import cn.donting.web.os.core.db.repository.IUserRepository;
import cn.donting.web.os.core.db.repository.IWapInfoRepository;
import cn.donting.web.os.core.db.repository.impl.json.OsFileTypeRepository;
import cn.donting.web.os.core.db.repository.impl.json.UserRepository;
import cn.donting.web.os.core.db.repository.impl.json.WapInfoRepository;
import cn.donting.web.os.launch.CoreOsApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfiguration {

    @Bean
    @ConditionalOnMissingBean(IUserRepository.class)
    public IUserRepository userRepository() {
        return new UserRepository();
    }
    @Bean
    @ConditionalOnMissingBean(IWapInfoRepository.class)
    public IWapInfoRepository wapInfoRepository() {
        return new WapInfoRepository();
    }
    @Bean
    @ConditionalOnMissingBean(IOsFileTypeRepository.class)
    public IOsFileTypeRepository osFileTypeRepository() {
        return new OsFileTypeRepository();
    }
}
