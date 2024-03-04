package cn.donting.web.os.api.user;

import java.util.Optional;

/**
 *
 */
public interface UserService {
    /**
     * 获取当前登录的用户
     * @return null 未登录
     */
    User getLoginUser();
}
