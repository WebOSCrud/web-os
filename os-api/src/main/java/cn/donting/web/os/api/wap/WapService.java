package cn.donting.web.os.api.wap;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * wap 管理器
 */
public interface WapService {
    /**
     * 根据文件 安装/更新 一个 wap
     * @param file
     * @return WapInfo
     */
    WapInfo installUpdate(File file) throws Exception;

    WapInfo getWapInfo(File file) throws Exception;



    /**
     * 根据 wapId卸载一个wap
     * @param wapId
     * @return 完成卸载
     */
    boolean uninstall(String wapId) throws Exception;


    /**
     * 获取所有已安装的wap列表
     * @return List<WapInfo>
     */
    List<WapInfo> getInstallList();

    /**
     * 获取wap 应用数据存储空间 目录，卸载时会一并删除
     * @param wapId
     * @return file
     */
    File getWapSpaces(String wapId);
}
