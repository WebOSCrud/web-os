package cn.donting.web.os.core.db.repository;

import cn.donting.web.os.core.db.OsDataBaseTable;
import cn.donting.web.os.core.db.entity.FileType;

import java.util.List;

public interface IOsFileTypeRepository extends OsDataBaseTable<FileType, String> {
    List<FileType> findByWapId(String wapId);
}
