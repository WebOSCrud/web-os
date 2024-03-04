package cn.donting.web.os.core.api;


import cn.donting.web.os.api.task.TaskService;
import cn.donting.web.os.core.service.UserService;
import cn.donting.web.os.core.service.WapService;
import org.springframework.stereotype.Component;

/**
 * @author donitng
 */
@Component
public class OsApi implements cn.donting.web.os.api.OsApi{
    private UserService userService;
    private WapService wapService;

    public OsApi(UserService userService, WapService wapService) {
        this.userService = userService;
        this.wapService = wapService;
    }

    @Override
    public UserService userService() {
        return userService;
    }

    @Override
    public WapService wapService() {
        return wapService;
    }

    @Override
    public TaskService taskService() {
        return null;
    }
}
