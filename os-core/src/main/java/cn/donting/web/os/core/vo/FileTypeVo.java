package cn.donting.web.os.core.vo;

import cn.donting.web.os.api.wap.FileType;
import cn.donting.web.os.core.db.entity.OsFileType;
import cn.donting.web.os.core.util.ResourceUtil;
import lombok.Data;


@Data
public class FileTypeVo {
    private String extName;
    private String description;
    private String wapId;
    private String iconUrl;
    private String openWindowIcon;

}
