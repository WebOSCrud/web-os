package cn.donting.web.os.api.wap;

import lombok.Data;

@Data
public class WapInstallInfo {
    private WapInfo wapInfo;
    private long installTime;
    private long updateTime;
}
