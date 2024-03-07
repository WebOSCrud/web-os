package cn.donting.web.os.core.db.repository;

import cn.donting.web.os.core.db.OsDataBaseTable;
import cn.donting.web.os.core.db.entity.OsFileType;
import cn.donting.web.os.core.db.entity.OsUser;

import java.util.List;

public interface IOsFileTypeRepository extends OsDataBaseTable<OsFileType> {
    @Override
    default String getId(OsFileType fileType) {
        return fileType.getExtName();
    }

   List<OsFileType> findByWapId(String wapId);
}
