package cn.donting.web.os.core.vo;

import cn.donting.web.os.core.db.entity.User;
import lombok.Data;

import static cn.donting.web.os.core.config.WapWebMvcConfigurer.USER_AVATAR_PATH;

@Data
public class UserVo {
    String name;
    String description;
    String avatarUrl;

    public UserVo(User user) {
        name=user.getName();
        description=user.getDescription();
        avatarUrl= USER_AVATAR_PATH+"/"+user.getAvatarName();
    }
}
