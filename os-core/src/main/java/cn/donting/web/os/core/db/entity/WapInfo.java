package cn.donting.web.os.core.db.entity;

import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.config.WapWebMvcConfigurer;
import cn.donting.web.os.core.db.DataId;
import cn.donting.web.os.core.vo.WapBaseInfoVo;
import cn.donting.web.os.core.vo.WapInfoVo;
import cn.donting.web.os.core.wap.Wap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.File;

/**
 * wap 安装信息
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class WapInfo extends cn.donting.web.os.api.wap.WapInfo implements DataId<String> {
    /**
     * 安装事件，相对于 OSFileSpaces#wapData 目录
     *
     * @see cn.donting.web.os.core.file.OSFileSpaces#WAP_DATA
     */
    private long installTime;
    /**
     * 更新时间，首次安装=0
     *
     * @see cn.donting.web.os.core.service.WapService#update(Wap, cn.donting.web.os.api.wap.WapInfo, File)
     */
    private long updateTime;

    @Override
    protected void finalize() throws Throwable {
        log.debug("WapInfo 卸载:{}", getId());
    }



}
