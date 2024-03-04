package cn.donting.web.os.api.wap;

/**
 * 窗口类型
 */
public enum WapWindowType {
    /**
     * 普通的窗口
     * 平平无奇
     *
     */
    Normal(false),
    /**
     * 桌面
     * 一般会带一个登陆 界面
     * Desktop 不是一个 窗口。是窗口容器
     */
    Desktop(true),
    /**
     * 文件(夹)选择
     *
     * 需要包含 文件选择，路径选择
     * 文件管理器 文件选择器
     * os.js.api.ts#  SelectFileOption
     * url 会传递 SelectFileOption
     *
     * @see SelectFileOption
     */
    FileManagerSelect(true),
    /**
     * 打开文件的窗口
     */
    OpenFile(true),
    ;

    /**
     * 唯一
     * 在wap 中只能出现一次
     */
    public final boolean unique;

    WapWindowType(boolean unique) {
        this.unique = unique;
    }
}
