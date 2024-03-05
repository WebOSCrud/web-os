package cn.donting.web.os.core.service;

import cn.donting.web.os.core.db.entity.User;
import cn.donting.web.os.core.db.repository.IUserRepository;
import cn.donting.web.os.core.exception.ResponseException;
import cn.donting.web.os.core.file.OSFileSpaces;
import cn.donting.web.os.core.util.FileUtil;
import cn.donting.web.os.core.vo.LoginForceVo;
import cn.donting.web.os.core.vo.ResponseBody;
import cn.donting.web.os.core.vo.ResponseBodyCodeEnum;
import cn.donting.web.os.core.vo.param.UserInfoPar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * user 管理器
 * 用于负责用户 登陆，注册，登出，自动登出
 * 限制为唯一登录，允许强制登录，自动下线之前的登陆
 * 登陆状态与 session 有关，登陆时长已 session 保存的时长有关
 * springboot session 配置  server.servlet.session.timeout
 * ApplicationRunner 监听 session 注销，用户自动登出
 *
 * @author donitng
 * @see org.springframework.boot.web.servlet.server.Session#setTimeout(Duration)
 * @see UserService#sessionDestroyed(HttpSessionEvent)
 */
@Service
@Slf4j
public class UserService implements cn.donting.web.os.api.user.UserService, HttpSessionListener, InitializingBean {

    final IUserRepository userRepository;
    final HttpServletRequest httpServletRequest;
    final HttpServletResponse httpServletResponse;

    public UserService(IUserRepository userRepository, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.userRepository = userRepository;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    /**
     * 根据 httpServletRequest 获取当前登陆的用户
     *
     * @return Optional<User>
     * @see cn.donting.web.os.api.user.User
     */
    @Override
    public User getLoginUser() {
        User user = (User) httpServletRequest.getSession().getAttribute("user");
        return user;
    }

//    /**
//     * 登陆用户
//     *
//     * @param userName 用户名
//     * @param password 密码
//     * @return null 登陆失败 {@link ResponseBodyCodeEnum#LOGIN_FAIL}
//     */
//    public synchronized User login(String userName, String password) {
//        if (userName == null || password == null) {
//            throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.LOGIN_FAIL));
//        }
//        Optional<User> userOp = userRepository.findById(userName);
//        if (userOp.isPresent() && userOp.get().getPassword().equals(password)) {
//            User user = userOp.get();
//            String token=UUID.randomUUID().toString();
//            user.setExpiredTime(System.currentTimeMillis()+User.tokenExpiredTime);
//            user.setToken(token);
//            userRepository.save(user);
//            httpServletRequest.getSession().setAttribute("user.token",token);
//            return user;
//        }
//        throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.LOGIN_FAIL));
//    }

    /**
     * 登出
     */
    public synchronized void logout() {
        User loginUser = getLoginUser();
        loginUser.setNonce(null);
        loginUser.setNonceExpiredTime(0);
        httpServletRequest.getSession().removeAttribute("user");
    }

    /**
     * 创建一个用户
     *
     * @param user
     * @return
     */
    public synchronized User creatUser(User user) {
        String name = user.getId();
        log.info("creatUser:{}", user.getName());
        if (userRepository.findById(name).isPresent()) {
            throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.LOGIN_CREAT_USE, user.getId() + "已经创建"));
        }
        user.setCreatTime(System.currentTimeMillis());
        File users = OSFileSpaces.users;
        File userSpace = new File(users, user.getName());
        userSpace.mkdirs();
        File desktop = new File(userSpace, OSFileSpaces.USER_DESKTOP_NAME);
        desktop.mkdirs();
        File file = new File(OSFileSpaces.OS_USER_AVATAR, user.getName() + ".png");
        try {
            user.setAvatarName(file.getName());
            URL resource = UserService.class.getClassLoader().getResource("static/img/defAccount.png");
            FileUtil.copyFile(resource, file);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
        return userRepository.save(user);
    }


    /**
     * 初始自动创建 root 用户
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Optional<User> userOp = userRepository.findById("root");
        if (userOp.isPresent()) {
            return;
        }
        //创建root 用户
        User user = new User();
        user.setName("root");
        user.setPassword("root");
        user.setDescription("Administrator");
        creatUser(user);
    }

    @Override
    public synchronized void sessionDestroyed(HttpSessionEvent se) {
        log.info("sessionDestroyed:{}", se.getSession().getId());
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.info("sessionCreated:{}", se.getSession().getId());
    }


    /**
     * 用户列表
     *
     * @return
     */
    public List<User> userList() {
        User loginUser = getLoginUser();
        if (!loginUser.getName().equals("root")) {
            throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.UNAUTHORIZED));
        }
        List<User> all = userRepository.findAll();
        for (User user : all) {
            user.setPassword(null);
            user.setNonceExpiredTime(0);
            user.setNonce(null);
        }
        return all;
    }

    /**
     * 修改用户信息
     *
     * @param userInfoPar
     * @return
     */
    public synchronized User modifyUserInfo(UserInfoPar userInfoPar) throws IOException {
        User loginUser = getLoginUser();
        User user = userRepository.findById(loginUser.getId()).get();
        if (userInfoPar.getPassword() != null) {
            if (userInfoPar.getOldPassword().equals(user.getPassword())) {
                throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.USER_OLD_PASSWORD_ERROR));
            }
            user.setPassword(userInfoPar.getPassword());
        }

        if (userInfoPar.getDescription() != null) {
            user.setDescription(userInfoPar.getDescription());
            loginUser.setDescription(userInfoPar.getDescription());
        }
        if (userInfoPar.getAvatarFilePath() != null) {
            //删除旧的
            if (user.getAvatarName() != null) {
                new File(OSFileSpaces.OS_USER_AVATAR, user.getAvatarName()).delete();
            }
            String avatarFilePath = userInfoPar.getAvatarFilePath();
            String extName = FileUtil.extName(new File(avatarFilePath));
            String avatarName = loginUser.getName() + "." + extName;
            FileUtil.copyFolder(new File(avatarFilePath).toPath(),
                    new File(OSFileSpaces.OS_USER_AVATAR, avatarName).toPath());
            user.setAvatarName(avatarName);
            loginUser.setAvatarName(avatarName);
        }
        userRepository.save(user);
        return user;
    }

    /**
     * 删除一个用户
     *
     * @param userName 用户名
     */
    public synchronized void deleteUser(String userName) {
        //不允许删除root
        if (userName.equals("root")) {
            throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.NOT_FOUND, "用户不存在"));
        }
        User loginUser = getLoginUser();
        if (!loginUser.getName().equals("root")) {
            throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.UNAUTHORIZED));
        }
        Optional<User> userOptional = userRepository.findById(userName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getAvatarName() != null) {
                new File(OSFileSpaces.OS_USER_AVATAR, user.getAvatarName()).delete();
            }
            //删除用户数据
            userRepository.deleteById(userName);
            File userSpace = new File(OSFileSpaces.users, user.getName());
            try {
                FileUtil.deleteFile(userSpace);
            } catch (IOException e) {
                log.warn("删除用户数据有残留数据：" + e.getMessage());
            }
        } else {
            throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.NOT_FOUND, "用户不存在"));
        }
    }
}
