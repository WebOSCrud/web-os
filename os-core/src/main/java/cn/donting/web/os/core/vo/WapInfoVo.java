package cn.donting.web.os.core.vo;


import cn.donting.web.os.api.wap.WapInfo;
import cn.donting.web.os.api.wap.WapWindow;
import cn.donting.web.os.core.util.ResourceUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @see cn.donting.web.os.core.db.entity.WapInfo
 */
@Data
public class WapInfoVo {

    private String id;

    private String name;
    /**
     * 可直接访问的 http url 地址
     */
    private String iconUrl;

    private String description;

    private String version;

    private int numberVersion;

    private List<WapWindowVo> wapWindows = new ArrayList<>();

    private List<FileTypeVo> fileTypes = new ArrayList<>();

    private long installTime;

    private long updateTime;

    public WapInfoVo(WapInfo wapInfo) {
        this.name = wapInfo.getName();
        this.id = wapInfo.getId();
        this.description = wapInfo.getDescription();
        this.version = wapInfo.getVersion();
        this.numberVersion = wapInfo.getNumberVersion();
        if(wapInfo instanceof cn.donting.web.os.core.db.entity.WapInfo){
            this.installTime = ((cn.donting.web.os.core.db.entity.WapInfo) wapInfo).getInstallTime();
            this.updateTime =  ((cn.donting.web.os.core.db.entity.WapInfo) wapInfo).getUpdateTime();
        }

        iconUrl = ResourceUtil.getWapResourceHttpURL(id, wapInfo.getIconResource());
        for (WapWindow wapWindow : wapInfo.getWapWindows()) {
            wapWindows.add(new WapWindowVo(id,wapWindow));
        }
        for (cn.donting.web.os.api.wap.WapInfo.FileType fileType : wapInfo.getFileTypes()) {
            fileTypes.add(new FileTypeVo(id,fileType));
        }
    }

    public WapInfoVo() {
    }
}
