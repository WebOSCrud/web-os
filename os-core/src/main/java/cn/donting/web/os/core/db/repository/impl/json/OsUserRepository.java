package cn.donting.web.os.core.db.repository.impl.json;

import cn.donting.web.os.core.db.entity.OsUser;
import cn.donting.web.os.core.db.entity.WapResource;
import cn.donting.web.os.core.db.repository.IOsUserRepository;
import cn.donting.web.os.core.db.repository.IWapResourceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class OsUserRepository extends JsonOsDataBaseTable<OsUser> implements IOsUserRepository {

    public OsUserRepository() {
        super("osUserRepository");
    }

    @Override
    protected TypeReference<Map<String, OsUser>> getTypeReference() {
        return new TypeReference<Map<String, OsUser>>() {
        };
    }

    @Override
    protected TypeReference<List<OsUser>> getTypeReferenceList() {
        return new TypeReference<List<OsUser>>() {
        };
    }


}
