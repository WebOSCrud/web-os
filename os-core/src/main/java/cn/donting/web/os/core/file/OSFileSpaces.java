package cn.donting.web.os.core.file;

import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.config.WapWebMvcConfigurer;
import cn.donting.web.os.core.service.WapService;

import java.io.File;

/**
 * OS数据存储空间
 * data
 * -users
 * --username
 * ---desktop
 * -wap
 * --wapId
 * --- ·*******.jar
 * --- ·icon.png
 * -os
 * --wap-resources
 * ----wapId
 *       ***.png
 *       ***.png
 * --db
 * --- ·installs.json 安装列表
 * --- ·setting.json 设置
 * --- ·fileSetting.json 默认文件打开设置
 */
public class OSFileSpaces {
    /**
     * 根路径
     */
    public static final File root = new File(System.getProperty("user.dir"), "data");
    /**
     * 用户空间
     * 每个用户有独立的 空间
     */
    public static final File users = new File(root, "users");
    /**
     * 全局 wap 数据空间
     * 还有 独立用户的数据
     */
    public static final File WAP_DATA = new File(root, "wap");
    public static final String WAP_DATA_NAME = "data";

    public static final File OS = new File(root, "os");
    /**
     * 用户头像目录
     */
    public static final File OS_USER_AVATAR = new File(OS, "avatar");
    public static final File OS_DB = new File(OS, "db");
    public static final File OS_WAP_RESOURCES = new File(OS, "wap-resources");

    public static final String USER_DESKTOP_NAME = "desktop";

    static {
        if (!root.exists()) {
            root.mkdirs();
        }
        if (!users.exists()) {
            users.mkdirs();
        }
        if (!WAP_DATA.exists()) {
            WAP_DATA.mkdirs();
        }
        if (!OS.exists()) {
            OS.mkdirs();
        }
        if (!OS_DB.exists()) {
            OS_DB.mkdirs();
        }
        if (!OS_WAP_RESOURCES.exists()) {
            OS_WAP_RESOURCES.mkdirs();
        }
        if (!OS_USER_AVATAR.exists()) {
            OS_USER_AVATAR.mkdirs();
        }
    }

    public static File getWapInstallFile(String wapId){
        File wapDir = new File(WAP_DATA, wapId);
        File installFIle = new File(wapDir, WapService.jarFileName);
        if(!installFIle.exists()){
            installFIle = new File(wapDir, WapService.devFileName);
        }
        return installFIle;
    }

}
