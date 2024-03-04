package cn.donting.web.os.api.wap;

import cn.donting.web.os.api.annotation.NonNull;
import cn.donting.web.os.api.annotation.Nullable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Wap 安装信息
 * <p>
 * 如果 {@link WapInfo.FileType} 存在， 则说明 wap 支持对应的 文件打开，
 * 则调用Wap 打开文件则访问 {@link WapWindowOption#url}，会携带 openFilePath参数到url上
 * </p>
 *
 *  Resource 结尾表示 是基于 classpath 读取资源的路径 {@link ClassLoader#getResource(String)}
 *  url 表示 浏览器 访问路径，不加wapId。
 *
 * @see WapWindowOption#url
 */
@Data
public class WapInfo {
    /**
     * 唯一id
     */
    @NonNull
    private String id;
    /**
     * 名称
     */
    @NonNull
    private String name;
    /**
     * 图标
     * 相对于 resources 路径
     * static/icon.png
     */
    @Nullable
    private String iconResource;
    /**
     * 描述
     */
    @NonNull
    private String description;
    /**
     * 版本标识
     */
    @NonNull
    private String version;
    /**
     * 数字版本，用于比较升级版本大小
     * 将不允许 降版本
     */
    @NonNull
    private int numberVersion;

    /**
     * 窗口参数
     * wap 启动的
     */
    @Nullable
    private List<WapWindow> wapWindows=new ArrayList<>();

    /**
     * 注册文件类型
     * 可用于显示 文件类型图标
     */
    @Nullable
    private List<FileType> fileTypes = new ArrayList<>();

    /**
     * 版本更新说明
     */
    @Nullable
    private String updateInstructions;

    /**
     * 文件类型
     */
    @Data
    public static class FileType {
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

}
