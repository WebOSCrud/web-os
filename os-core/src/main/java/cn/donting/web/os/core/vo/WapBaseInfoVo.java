package cn.donting.web.os.core.vo;

import cn.donting.web.os.core.db.entity.WapInfo;
import cn.donting.web.os.core.file.OSFileSpaces;
import cn.donting.web.os.core.util.ResourceUtil;
import lombok.Data;

@Data
public class WapBaseInfoVo {
    private String name;
    private String iconUrl;
    private String wapId;

    public WapBaseInfoVo(WapInfo wapInfo) {
        name=wapInfo.getName();
        wapId=wapInfo.getId();
        iconUrl= ResourceUtil.getWapResourceHttpURL(wapId,wapInfo.getIconResource());
    }
}
