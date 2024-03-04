package cn.donting.web.os.core.db.repository.impl.json;

import cn.donting.web.os.core.db.entity.FileType;
import cn.donting.web.os.core.db.repository.IOsFileTypeRepository;
import cn.donting.web.os.core.file.OSFileSpaces;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.Map;

public class OsFileTypeRepository extends JsonOsDataBaseTable<FileType, String> implements IOsFileTypeRepository {

    public OsFileTypeRepository() {
        super(new File(OSFileSpaces.OS_DB, "fileTypes.json"));
    }

    @Override
    protected TypeReference<Map<String, FileType>> getTypeReference() {
        return new TypeReference<Map<String, FileType>>() {
        };
    }

    @Override
    protected TypeReference<List<FileType>> getTypeReferenceList() {
        return new TypeReference<List<FileType>>() {
        };
    }

    public List<FileType> findByWapId(String wapId) {
        List<FileType> all = super.findAll();
        all.removeIf(fileType -> !fileType.getId().equals(wapId));
        return all;
    }
}
