package cn.donting.web.os.core.service;

import cn.donting.web.os.api.spaces.FileSpaces;
import cn.donting.web.os.api.user.User;
import cn.donting.web.os.core.file.OSFileSpaces;
import org.springframework.stereotype.Service;

import java.io.File;
@Service
public class FileSpacesService implements FileSpaces {
    @Override
    public File getDesktopSpacesFolder(User user) {
        File users = OSFileSpaces.users;
        File userSpace = new File(users, user.getName());
        File desktop = new File(userSpace, OSFileSpaces.USER_DESKTOP_NAME);
        return desktop;
    }
}
