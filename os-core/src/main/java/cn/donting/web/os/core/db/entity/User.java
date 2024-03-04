package cn.donting.web.os.core.db.entity;

import cn.donting.web.os.core.db.DataId;
import cn.donting.web.os.core.vo.UserVo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends cn.donting.web.os.api.user.User implements DataId<String> {
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

    @Override
    @JsonIgnore
    public String getId() {
        return super.getName();
    }



}
