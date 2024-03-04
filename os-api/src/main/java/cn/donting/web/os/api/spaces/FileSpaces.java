package cn.donting.web.os.api.spaces;

import cn.donting.web.os.api.user.User;

import java.io.File;

/**
 * 通用文件空间
 */
public interface FileSpaces {
    /**
     * 桌面路径
     * @return 桌面文件夹
     */
    File getDesktopSpacesFolder(User user);
}
