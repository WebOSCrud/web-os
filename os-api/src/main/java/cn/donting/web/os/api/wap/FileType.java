package cn.donting.web.os.api.wap;

import cn.donting.web.os.api.annotation.NonNull;
import lombok.Data;

/**
 * 文件类型
 */
@Data
public  class FileType {
    /**
     * 未知文件（无扩展名）
     */
    public static final String ext_name_unknown = "";
    /**
     * 文件夹
     */
    public static final String ext_name_Directory = "__directory";
    /**
     * 文件图标
     * 相对于 resources 路径
     * static/icon.png
     */
    @NonNull
    private String iconResource;

    /**
     * 文件描述
     */
    @NonNull
    private String description;
    /**
     * 文件扩展名
     */
    @NonNull
    private String extName;
}
