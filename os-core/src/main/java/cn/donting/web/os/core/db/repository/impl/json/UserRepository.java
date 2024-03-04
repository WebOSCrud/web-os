package cn.donting.web.os.core.db.repository.impl.json;

import cn.donting.web.os.core.db.entity.User;
import cn.donting.web.os.core.db.repository.IUserRepository;
import cn.donting.web.os.core.file.OSFileSpaces;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.List;
import java.util.Map;


public class UserRepository extends JsonOsDataBaseTable<User,String> implements IUserRepository {
    public UserRepository() {
        super(new File(OSFileSpaces.OS_DB,"user.json"));
    }

    @Override
    protected TypeReference<Map<String, User>> getTypeReference() {
        return new TypeReference<Map<String, User>>() {
        };
    }

    @Override
    protected TypeReference<List<User>> getTypeReferenceList() {
        return new TypeReference<List<User>>() {
        };
    }
}
