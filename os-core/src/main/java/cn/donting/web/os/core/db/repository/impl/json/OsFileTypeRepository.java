package cn.donting.web.os.core.db.repository.impl.json;

import cn.donting.web.os.api.wap.FileType;
import cn.donting.web.os.core.db.entity.OsFileType;
import cn.donting.web.os.core.db.entity.OsUser;
import cn.donting.web.os.core.db.entity.WapResource;
import cn.donting.web.os.core.db.repository.IOsFileTypeRepository;
import cn.donting.web.os.core.db.repository.IOsUserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class OsFileTypeRepository extends JsonOsDataBaseTable<OsFileType> implements IOsFileTypeRepository {

    public OsFileTypeRepository() {
        super("osFileType");
    }

    @Override
    protected TypeReference<Map<String, OsFileType>> getTypeReference() {
        return new TypeReference<Map<String, OsFileType>>() {
        };
    }

    @Override
    protected TypeReference<List<OsFileType>> getTypeReferenceList() {
        return new TypeReference<List<OsFileType>>() {
        };
    }


    @Override
    public List<OsFileType> findByWapId(String wapId) {
        List<OsFileType> osFileTypes = findAll().stream().filter(wr -> wr.getWapId().equals(wapId)).collect(Collectors.toList());
        return osFileTypes;
    }
}
