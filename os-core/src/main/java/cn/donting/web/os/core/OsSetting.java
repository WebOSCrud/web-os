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
     * 文件(夹)选择 的wapId
     */
    private String fileManagerSelectWapId;


}
