package cn.donting.web.os.core.db.entity;

import cn.donting.web.os.api.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OsUser extends User  {

    /**
     * token 过期时间30分钟
     */
    public static final long NONCE_EXPIRED_TIME=1000*60*30;

    /**
     * 用户密码
     * 加密？
     *
     */
    private String password;
    /**
     * 用户头像
     */
    private String avatarName;
    /**
     * 访问token
     */
    private String nonce;
    /**
     * token 过期时间
     */
    private long nonceExpiredTime;





}
