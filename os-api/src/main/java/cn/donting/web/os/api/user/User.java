package cn.donting.web.os.api.user;

import lombok.Data;

/**
 * os 系统用户
 */
@Data
public class User {
    /**
     * 用户名
     */
    String name;
    /**
     * 用户描述
     */
    String description;
    /**
     * 创建时间
     */
    long creatTime;
}
