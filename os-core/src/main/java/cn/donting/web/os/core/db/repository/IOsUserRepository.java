package cn.donting.web.os.core.db.repository;

import cn.donting.web.os.core.db.OsDataBaseTable;
import cn.donting.web.os.core.db.entity.OsUser;

import java.util.Optional;

public interface IOsUserRepository extends OsDataBaseTable<OsUser> {
    @Override
    default String getId(OsUser osUser) {
        return osUser.getName();
    }
}
