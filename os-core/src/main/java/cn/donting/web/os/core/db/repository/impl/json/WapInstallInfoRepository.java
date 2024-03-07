package cn.donting.web.os.core.db.repository.impl.json;

import cn.donting.web.os.api.wap.WapInstallInfo;
import cn.donting.web.os.core.db.repository.IWapInstallInfoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class WapInstallInfoRepository extends JsonOsDataBaseTable<WapInstallInfo> implements IWapInstallInfoRepository {

    public WapInstallInfoRepository() {
        super("wapInstallInfo");
    }

    @Override
    protected TypeReference<Map<String, WapInstallInfo>> getTypeReference() {
        return new TypeReference<Map<String, WapInstallInfo>>() {
        };
    }

    @Override
    protected TypeReference<List<WapInstallInfo>> getTypeReferenceList() {
        return new TypeReference<List<WapInstallInfo>>() {
        };
    }
}
