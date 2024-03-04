package cn.donting.web.os.api.wap;

import cn.donting.web.os.api.annotation.NonNull;
import cn.donting.web.os.api.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 * 窗口参数
 * 打开窗口时的参数
 *
 * @see WapWindow
 */
@Getter
@Setter
public class WapWindowOption {
    /**
     * 窗口打开的url
     * 不带 wapId
     * spring boot 原始访问路径是啥就是啥。 访问时会自动加 wapId
     * /index.html
     * 如果是使用打开文件 则会跟上 openFilePath 的参数
     * static/index.html?openFilePath=D:\abc.tet
     *
     * @see WapWindowType#OpenFile
     */
    @NonNull
    private String url;

    /**
     * 标题
     */
    @NonNull
    private String title;

    /**
     * 窗口图标
     */
    @Nullable
    private String iconResource;

    /**
     * 宽度
     */
    @Nullable
    private Integer width;

    /**
     * 高度
     */
    @Nullable
    private Integer height;

    /**
     * X偏移量，默认居中
     */
    @Nullable
    private Integer x;

    /**
     * Y偏移量，默认居中
     */
    @Nullable
    private Integer y;

    /**
     * 最小宽度，默认值为0
     */
    @Nullable
    private Integer minWidth;

    /**
     * 最小高度，默认值为0
     */
    @Nullable
    private Integer minHeight;

    /**
     * 最大宽度，默认值不限
     */
    @Nullable
    private Integer maxWidth;

    /**
     * 最大高度，默认值不限
     */
    @Nullable
    private Integer maxHeight;

    /**
     * 窗口大小是否可调整，默认值为true
     */
    @Nullable
    private Boolean resizable = true;

    /**
     * 窗口是否可移动，默认值为true
     */
    @Nullable

    private Boolean movable = true;

    /**
     * 窗口是否可最小化，默认值为true
     */
    @Nullable
    private Boolean minimizable = true;

    /**
     * 窗口是否最大化，默认值为true
     */
    @Nullable
    private Boolean maximizable = true;

    /**
     * 窗口主题背景色
     * 默认桌面设置
     */
    @Nullable
    private String background;

}
