package cn.donting.web.os.core.db.repository.impl.json;

import cn.donting.web.os.core.db.entity.WapInfo;
import cn.donting.web.os.core.db.repository.IUserRepository;
import cn.donting.web.os.core.db.repository.IWapInfoRepository;
import cn.donting.web.os.core.file.OSFileSpaces;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.Map;

public class WapInfoRepository extends JsonOsDataBaseTable<WapInfo, String> implements IWapInfoRepository {
    public WapInfoRepository() {
        super(new File(OSFileSpaces.OS_DB, "wapInfo.json"));
    }

    @Override
    protected TypeReference<Map<String, WapInfo>> getTypeReference() {
        return new TypeReference<Map<String, WapInfo>>() {
        };
    }

    @Override
    protected TypeReference<List<WapInfo>> getTypeReferenceList() {
        return new TypeReference<List<WapInfo>>() {
        };
    }
}
