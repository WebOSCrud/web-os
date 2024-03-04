package cn.donting.web.os.core.vo;

import cn.donting.web.os.core.db.entity.WapInfo;
import cn.donting.web.os.core.wap.Wap;
import cn.donting.web.os.core.wap.WapStatus;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @see cn.donting.web.os.core.wap.Wap
 */
@Data
public class WapVo {
    private long httpLastVisitTimme;
    /**
     * 启动时间
     */
    private long startTime;
    /**
     * 启动耗时
     */
    private long startUpTime;
    /**
     * 活动的线程数
     */
    private int activeThread;

    private WapStatus wapStatus;

    WapInfoVo wapInfo;

    public WapVo(Wap wap) {
        httpLastVisitTimme=wap.getHttpLastVisitTimme();
        startTime=wap.getStartTime();
        startUpTime=wap.getStartUpTime();
        wapStatus=wap.getWapStatus();
        WapInfo wapInfo = wap.getWapInfo();
        this.wapInfo=new WapInfoVo(wapInfo);
        ThreadGroup threadGroup = wap.getWapClassLoader().getThreadGroup();
        activeThread = threadGroup.activeCount();
    }
}
