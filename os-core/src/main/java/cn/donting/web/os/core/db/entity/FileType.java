package cn.donting.web.os.core.db.entity;

import cn.donting.web.os.core.db.DataId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.nio.file.Files;

/**
 * 文件类型
 * Os 设置的 默认 文件类型
 *
 * @see cn.donting.web.os.api.wap.WapInfo#fileTypes
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class  FileType implements DataId<String> {
    /**
     * 未知文件（无扩展名）
     */
    public static final String ext_name_unknown = "";
    /**
     * 文件夹
     */
    public static final String ext_name_Directory = "__directory";
    /**
     * 扩展名
     */
    String extName;
    /**
     * wap id
     */
    String wapId;
    /**
     * static/icon.png
     * @see WapInfo#fileTypes
     * @see cn.donting.web.os.api.wap.WapInfo.FileType#iconResource
     */
    String iconResource;
    /**
     * 文件类型的描述
     *
     */
    String description;

    public void setExtName(String extName) {
        this.extName = extName.toLowerCase();
    }

    @Override
    public String getId() {
        return extName;
    }
}
