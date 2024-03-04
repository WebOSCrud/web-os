package cn.donting.web.os.core.controller;

import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.api.OsApi;
import cn.donting.web.os.core.db.entity.User;
import cn.donting.web.os.core.service.UserService;
import cn.donting.web.os.core.vo.LoginForceVo;
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

    /**
     * 用户登录
     *
     * @param user name,password
     * @return {@link  ResponseBodyCodeEnum#LOGIN_IS_LOGIN}
     * {@link  ResponseBodyCodeEnum#LOGIN_FAIL}
     * {@link  ResponseBodyCodeEnum#OK}
     * @see
     */
    @PostMapping("/login")
    public ResponseBody<UserVo> login(@RequestBody User user) {
        user = osApi.userService().login(user.getName(), user.getPassword());
        return ResponseBody.success(new UserVo(user));
    }

    /**
     * 强制登陆
     *
     * @param LoginParamVo 强制登陆id,来自  /user/login
     * @return {@link  ResponseBodyCodeEnum#LOGIN_FAIL}
     * {@link  ResponseBodyCodeEnum#OK}
     */
    @PostMapping("/login/force")
    public ResponseBody<UserVo> loginForce(@RequestBody LoginForceVo LoginParamVo) {
        User user = osApi.userService().loginForce(LoginParamVo.getLoginId());
        return ResponseBody.success(new UserVo(user));
    }

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
        User loginUser = userService.getLoginUser();
        return ResponseBody.success(new UserVo(loginUser));
    }


    /**
     * 用户列表
     *
     * @return {@link  ResponseBodyCodeEnum#UNAUTHORIZED}
     */
    @GetMapping("/list")
    public ResponseBody<List<UserVo>> userList() {
        List<User> users = userService.userList();
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
        User user = userService.modifyUserInfo(userInfoPar);
        return ResponseBody.success(new UserVo(user));
    }

    /**
     * 获取用户头像
     *
     * @return
     */
    @GetMapping("/avatar")
    public void userAvatar(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        User loginUser = userService.getLoginUser();
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
