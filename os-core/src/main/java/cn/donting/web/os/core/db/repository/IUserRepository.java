package cn.donting.web.os.core.db.repository;

import cn.donting.web.os.core.db.DataId;
import cn.donting.web.os.core.db.OsDataBaseTable;
import cn.donting.web.os.core.db.entity.User;

import java.util.Optional;

public interface IUserRepository extends OsDataBaseTable<User,String> {
    Optional<User> findByToken(String token);
}
