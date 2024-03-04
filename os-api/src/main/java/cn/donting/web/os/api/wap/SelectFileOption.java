package cn.donting.web.os.api.wap;

import cn.donting.web.os.api.annotation.Nullable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择文件的参数
 *
 * @see WapWindowType#FileManagerSelect
 */
@Data
public class SelectFileOption {
    /**
     * 允许选择文件
     * def=true
     */
    private boolean openFile = true;

    /**
     * 允许选择文件夹
     * def=true
     */
    private boolean openDirectory = true;

    /**
     * 允许多选
     * def=false
     */
    private boolean multi = false;

    /**
     * 允许显示隐藏的文件/文件夹
     * def=true
     */
    private boolean showHiddenFiles = true;

    /**
     * 窗口标题
     */
    @Nullable
    private String title;

    /**
     * 默认选择的路径
     */
    @Nullable
    private String defaultPath;

    /**
     * 仅显示自定义的扩展名
     */
    @Nullable
    private List<String> fileFilters=new ArrayList<>();

}
