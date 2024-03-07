package cn.donting.web.os.core.db.repository;

import cn.donting.web.os.api.wap.WapInstallInfo;
import cn.donting.web.os.core.db.OsDataBaseTable;
import cn.donting.web.os.core.db.entity.WapResource;

import java.util.List;

public interface IWapResourceRepository extends OsDataBaseTable<WapResource> {
    default String getId(WapResource wapResource){
        return wapResource.getId();
    }

     List<WapResource> findByWapId(String wapId);

}
