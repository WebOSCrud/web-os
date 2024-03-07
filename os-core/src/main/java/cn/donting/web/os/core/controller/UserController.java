package cn.donting.web.os.core.controller;

import cn.donting.web.os.core.api.OsApi;
import cn.donting.web.os.core.db.entity.OsUser;
import cn.donting.web.os.core.service.UserService;
import cn.donting.web.os.core.vo.ResponseBody;
import cn.donting.web.os.core.vo.ResponseBodyCodeEnum;
import cn.donting.web.os.core.vo.UserVo;
import cn.donting.web.os.core.vo.param.UserInfoPar;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static cn.donting.web.os.core.config.WapWebMvcConfigurer.USER_AVATAR_PATH;

/**
 * 用户相关的控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final OsApi osApi;
    private final UserService userService;

    public UserController(OsApi osApi, UserService userService) {
        this.osApi = osApi;
        this.userService = userService;
    }
//
//    /**
//     * 用户登录
//     *
//     * @param user name,password
//     * @return {@link  ResponseBodyCodeEnum#LOGIN_IS_LOGIN}
//     * {@link  ResponseBodyCodeEnum#LOGIN_FAIL}
//     * {@link  ResponseBodyCodeEnum#OK}
//     * @see
//     */
//    @PostMapping("/login")
//    public ResponseBody<UserVo> login(@RequestBody User user) {
//        user = osApi.userService().login(user.getName(), user.getPassword());
//        return ResponseBody.success(new UserVo(user));
//    }


    /**
     * 登出
     *
     * @return {@link  ResponseBodyCodeEnum#OK}
     */
    @PostMapping("/logout")
    public ResponseBody<Void> logout() {
        osApi.userService().logout();
        return ResponseBody.success();
    }

    /**
     * 获取登陆的用户信息
     *
     * @return {@link  ResponseBodyCodeEnum#OK}
     */
    @GetMapping("/login/user")
    public ResponseBody<UserVo> loginUser() {
        OsUser loginUser = userService.getLoginUser();
        return ResponseBody.success(new UserVo(loginUser));
    }


    /**
     * 用户列表
     *
     * @return {@link  ResponseBodyCodeEnum#UNAUTHORIZED}
     */
    @GetMapping("/list")
    public ResponseBody<List<UserVo>> userList() {
        List<OsUser> users = userService.userList();
        List<UserVo> collect = users.stream().map(UserVo::new).collect(Collectors.toList());
        return ResponseBody.success(collect);
    }

    /**
     * 修改用户信息
     *
     * @return
     */
    @PostMapping("/info")
    public ResponseBody<UserVo> userInfo(@RequestBody UserInfoPar userInfoPar) throws IOException {
        OsUser user = userService.modifyUserInfo(userInfoPar);
        return ResponseBody.success(new UserVo(user));
    }

    /**
     * 获取用户头像
     *
     * @return
     */
    @GetMapping("/avatar")
    public void userAvatar(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        OsUser loginUser = userService.getLoginUser();
        String avatarName = loginUser.getAvatarName();
        httpServletRequest.getRequestDispatcher(USER_AVATAR_PATH + "/" + avatarName)
                .forward(httpServletRequest, httpServletResponse);

    }


    /**
     * 删除用户
     *
     * @return
     */
    @DeleteMapping()
    public ResponseBody deleteUser(String userName) throws IOException, ServletException {
        userService.deleteUser(userName);
        return ResponseBody.success();
    }
}
