package cn.donting.web.os.core.db.repository;

import cn.donting.web.os.api.wap.WapInstallInfo;
import cn.donting.web.os.core.db.OsDataBaseTable;

public interface IWapInstallInfoRepository extends OsDataBaseTable<WapInstallInfo> {
    default String getId(WapInstallInfo wapInstallInfo){
        return wapInstallInfo.getWapInfo().getId();
    }
}
