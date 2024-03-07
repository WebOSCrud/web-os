package cn.donting.web.os.core.vo;

import cn.donting.web.os.api.wap.WapInstallInfo;
import cn.donting.web.os.core.util.ResourceUtil;
import lombok.Data;

@Data
public class WapBaseInfoVo {
    private String name;
    private String iconUrl;
    private String wapId;

    public WapBaseInfoVo(WapInstallInfo wapInfo) {
        name=wapInfo.getWapInfo().getName();
        wapId=wapInfo.getWapInfo().getId();
        iconUrl= ResourceUtil.getWapResourceHttpURL(wapId,wapInfo.getWapInfo().getIconResource());
    }
}
