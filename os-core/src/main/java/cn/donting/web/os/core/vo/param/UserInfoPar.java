package cn.donting.web.os.core.vo.param;

import lombok.Data;

@Data
public class UserInfoPar {
    String oldPassword;
    String password;
    String avatarFilePath;
    String description;
}
