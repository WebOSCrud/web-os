package cn.donting.web.os.core.vo;

import cn.donting.web.os.api.wap.WapWindowOption;
import cn.donting.web.os.core.file.OSFileSpaces;
import cn.donting.web.os.core.util.ResourceUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

/**
 * 打开文件 的 窗口参数
 */
@Getter
@Setter
public class WapWindowOptionVo {

    private WapWindowOption wapWindowOption;

}
