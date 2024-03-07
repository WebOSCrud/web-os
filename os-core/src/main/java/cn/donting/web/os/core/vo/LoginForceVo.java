package cn.donting.web.os.core.vo;

import cn.donting.web.os.core.service.UserService;
import cn.donting.web.os.core.db.entity.OsUser;
import lombok.Data;

/**
 * 强制登陆参数
 * @see cn.donting.web.os.core.controller.UserController#login(OsUser)
 * @see cn.donting.web.os.core.controller.UserController#loginForce(LoginForceVo)
 * @see UserService#login(String, String)
 * @see UserService#loginForce(String)
 * @see UserService#USER_LOGIN_FORCE
 * @see ResponseBodyCodeEnum#LOGIN_IS_LOGIN
 *
 * @author donting
 */
@Data
public class LoginForceVo {
    /**
     * 登录id
     * @see UserService#loginForce(String)
     * @see UserService#login(String, String)
     */
    private String loginId;
}
