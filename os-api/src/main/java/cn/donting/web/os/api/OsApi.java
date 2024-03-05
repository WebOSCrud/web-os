package cn.donting.web.os.api;

import cn.donting.web.os.api.spaces.FileSpaces;
import cn.donting.web.os.api.task.TaskService;
import cn.donting.web.os.api.user.UserService;
import cn.donting.web.os.api.wap.WapService;

public interface OsApi {
    UserService userService();

    WapService wapService();

    TaskService taskService();
    FileSpaces fileSpaces();
}
