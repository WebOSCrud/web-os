package cn.donting.web.os.api.wap;

/**
 * 窗口类型
 */
public enum WapWindowType {
    /**
     * 普通的窗口
     * 平平无奇
     */
    Normal,
    /**
     * 用于打开文件的窗口
     * 注册了 {@link FileType} 则必须包含一个（唯一一个）OpenFile类型的窗口
     * 然后使用 js使用 wen.os.js.api.creatWindow().args().filePath 获取要打开的文件路径
     */
    OpenFile,
    /**
     * 桌面
     * Desktop 不是一个 窗口。是窗口容器
     */
    Desktop;
}
