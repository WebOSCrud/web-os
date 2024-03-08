package cn.donting.web.os.core.vo;

import cn.donting.web.os.api.wap.WapInfo;
import cn.donting.web.os.api.wap.WapInstallInfo;
import cn.donting.web.os.core.wap.Wap;
import cn.donting.web.os.core.wap.WapStatus;
import lombok.Data;

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

    WapInstallInfo wapInstallInfo;

    public WapVo(Wap wap,WapInstallInfo wapInstallInfo) {
        httpLastVisitTimme=wap.getHttpLastVisitTimme();
        startTime=wap.getStartTime();
        startUpTime=wap.getStartUpTime();
        wapStatus=wap.getWapStatus();
        this.wapInstallInfo=wapInstallInfo;
        ThreadGroup threadGroup = wap.getWapClassLoader().getThreadGroup();
        activeThread = threadGroup.activeCount();
    }
}
