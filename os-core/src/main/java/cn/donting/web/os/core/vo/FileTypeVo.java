package cn.donting.web.os.core.vo;

import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.db.entity.FileType;
import cn.donting.web.os.core.util.ResourceUtil;
import lombok.Data;


@Data
public class FileTypeVo {
    private String extName;
    private String description;
    private String wapId;
    private String iconUrl;

    public FileTypeVo(FileType fileType) {
        this.extName = fileType.getExtName();
        this.wapId = fileType.getWapId();
        this.description = fileType.getDescription();
        iconUrl=ResourceUtil.getWapResourceHttpURL(wapId,fileType.getIconResource());
    }
    public FileTypeVo(String wapId,cn.donting.web.os.api.wap.WapInfo.FileType fileType) {
        this.extName = fileType.getExtName();
        this.wapId = wapId;
        this.description = fileType.getDescription();
        iconUrl=ResourceUtil.getWapResourceHttpURL(wapId,fileType.getIconResource());
    }
}
