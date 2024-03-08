package cn.donting.web.os.api.wap;

import cn.donting.web.os.api.annotation.NonNull;
import cn.donting.web.os.api.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 * 窗口参数
 * 打开窗口时默认的参数
 * 和 web.os.js.api 中一致
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
     * 窗口打开的url
     */
    @NonNull
    String url;
    /**
     * 标题
     * 默认 桌面环境定义
     */
    @Nullable
    String title;

    /**
     * 窗口图标
     * 默认 桌面环境定义
     */
    @Nullable
    String iconUrl;
    /**
     * 宽度
     * 默认 桌面环境定义
     */
    @Nullable
    Integer width;
    /**
     * 高度
     * 默认 桌面环境定义
     */
    @Nullable
    Integer height;
    /**
     * x 偏移量
     * 默认居中
     */
    @Nullable
    Integer x;
    /**
     * y 偏移量
     * 默认居中
     */
    @Nullable
    Integer y;
    /**
     * 最小宽度
     * 默认值为 0.
     */
    @Nullable
    Integer minWidth;
    /**
     * 最小高度
     * 默认值为 0.
     */
    @Nullable
    Integer minHeight;
    /**
     * 最大宽度
     * 默认值不限
     */
    @Nullable
    Integer maxWidth;
    /**
     * 最大高度
     * 默认值不限
     */
    @Nullable
    Integer maxHeight;
    /**
     * 窗口大小是否可调整
     * 默认值为 true
     */
    @Nullable
    Boolean resizable;
    /**
     * 窗口是否可移动
     * 默认值为 true
     */
    @Nullable
    Boolean movable;
    /**
     * 窗口是否可最小化
     * 默认值为 true
     */
    @Nullable
    Boolean minimizable;
    /**
     * 窗口是否最大化
     * 默认值为 true
     */
    @Nullable
    Boolean maximizable;
    /**
     * 窗口主题背景色
     * 默认 桌面环境定义
     */
    @Nullable
    String background;

}
