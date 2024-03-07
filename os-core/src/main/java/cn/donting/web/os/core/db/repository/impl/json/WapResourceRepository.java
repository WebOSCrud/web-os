package cn.donting.web.os.core.db.repository.impl.json;

import cn.donting.web.os.api.wap.WapInstallInfo;
import cn.donting.web.os.core.db.entity.WapResource;
import cn.donting.web.os.core.db.repository.IWapInstallInfoRepository;
import cn.donting.web.os.core.db.repository.IWapResourceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class WapResourceRepository extends JsonOsDataBaseTable<WapResource> implements IWapResourceRepository {

    public WapResourceRepository() {
        super("wapResource");
    }

    @Override
    protected TypeReference<Map<String, WapResource>> getTypeReference() {
        return new TypeReference<Map<String, WapResource>>() {
        };
    }

    @Override
    protected TypeReference<List<WapResource>> getTypeReferenceList() {
        return new TypeReference<List<WapResource>>() {
        };
    }

    @Override
    public List<WapResource> findByWapId(String wapId) {
        List<WapResource> ids = findAll().stream().filter(wr -> wr.getWapId().equals(wapId)).collect(Collectors.toList());
        return ids;
    }
}
