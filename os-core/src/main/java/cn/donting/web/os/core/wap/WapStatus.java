package cn.donting.web.os.core.wap;

/**
 * wap 状态
 */
public enum WapStatus {
    /**
     * 加载中，从文件加载为Wap
     */
    Loading,
    /**
     * 从文件加载完成成为一个Wap
     */
    Loaded,
    /**
     * spring 容器启动中
     */
    Starting,
    /**
     * spring 容器运中
     */
    Running,
    /**
     * spring 容器 停止
     */
    Stopping,
    /**
     * spring 容器 停止完成
     * wap 收尾完成，等待 jvm 卸载classloader
     *
     */
    Stopped,
}
