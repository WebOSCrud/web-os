package cn.donting.web.os.api.wap;

import cn.donting.web.os.api.annotation.NonNull;
import cn.donting.web.os.api.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * wap 窗口
 * 用于注册 窗口， 可直接打开 注册窗口，并可以从os 创建快方式
 * 并不是说一定要注册的 窗口才能打开，也可以通过 os.js.api 打开自定义的窗口
 * 对于 桌面{@link WapWindowType#Desktop}以及 文件选择窗口 {@link  WapWindowType#FileManagerSelect} 窗口来说，一定要注册，应为os 需要注册 信息来完成一些 操作
 * @author donting
 */
@Getter
@Setter
@ToString(callSuper = true)
public class WapWindow extends WapWindowOption{
    /**
     * 窗口名称
     */
    @NonNull
    private String name;

    /**
     * 描述
     */
    @Nullable
    private String description;
    /**
     * 窗口类型
     */
    @NonNull
    private WapWindowType type;

    /**
     * 桌面 登陆界面 需要的 非登陆授权资源
     *  type={@link WapWindowType#Desktop} 时生效
     * 是 GET 的访问 路径，不带wapId。实际 wap 问路径 变成  /wap/login.html
     * 以/开始
     * ['/login.html','/login.js','/login.css']
     * @see WapWindowType#Desktop
     */
    @Nullable
    private List<String> loginIgnoreURL=new ArrayList<>();

}
