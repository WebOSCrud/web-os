package cn.donting.web.os.core.api;


import cn.donting.web.os.api.spaces.FileSpaces;
import cn.donting.web.os.api.task.TaskService;
import cn.donting.web.os.core.service.FileSpacesService;
import cn.donting.web.os.core.service.UserService;
import cn.donting.web.os.core.service.WapService;
import cn.donting.web.os.launch.CoreOsApi;
import org.springframework.stereotype.Component;

/**
 * @author donitng
 */
@Component
public class OsApi implements cn.donting.web.os.api.OsApi{
    private UserService userService;
    private WapService wapService;
    private FileSpacesService fileSpacesService;

    public OsApi(UserService userService, WapService wapService,FileSpacesService fileSpacesService) {
        this.userService = userService;
        this.wapService = wapService;
        this.fileSpacesService=  fileSpacesService;
        CoreOsApi.setOsApi(this);
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

    public FileSpaces fileSpaces(){
        return fileSpacesService;
    }
}
