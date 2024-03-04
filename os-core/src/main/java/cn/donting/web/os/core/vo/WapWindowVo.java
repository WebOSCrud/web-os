package cn.donting.web.os.core.vo;

import cn.donting.web.os.api.wap.WapWindow;
import cn.donting.web.os.core.util.ResourceUtil;
import cn.donting.web.os.core.util.UrlUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

/**
 *
 */
@JsonIgnoreProperties({"loginIgnoreURL","iconResource"})
public class WapWindowVo extends WapWindow {
    @Getter
    @Setter
    private String iconUrl;
    public WapWindowVo(String wapId,WapWindow wapWindow) {
        BeanUtils.copyProperties(wapWindow,this);
        this.iconUrl= ResourceUtil.getWapResourceHttpURL(wapId,wapWindow.getIconResource());
        setUrl(UrlUtil.urlToWapUrl(wapId,wapWindow.getUrl()));
    }
}
