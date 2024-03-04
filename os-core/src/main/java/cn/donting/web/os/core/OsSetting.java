package cn.donting.web.os.core;

import cn.donting.web.os.api.wap.WapWindow;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统设置
 */
@Data
public class OsSetting {
    /**
     * 默认桌面 wapID
     */
    private String desktopWapId;
    /**
     *  桌面 wap 忽略的登陆 资源
     * @see WapWindow#loginIgnoreURL
     */
    private List<String> loginIgnoreRURL;
    /**
     * 文件(夹)选择 的wapId
     */
    private String fileManagerSelectWapId;


}
