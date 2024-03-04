package cn.donting.web.os.core.db.repository.impl.json;

import cn.donting.web.os.core.db.DataId;
import cn.donting.web.os.core.db.OsDataBaseTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * json 数据库实现 的 实现
 * 数据不多，json 保存在内存里不考虑性能
 *
 * @param <T>
 * @param <ID>
 */
@Slf4j
public abstract class JsonOsDataBaseTable<T extends DataId<ID>, ID> implements OsDataBaseTable<T, ID> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Map<ID, T> dataBase;
    private File dbFile;

    public JsonOsDataBaseTable(File dbFile) {
        this.dbFile = dbFile;
        dataBase = new ConcurrentHashMap<>();
        if (dbFile.exists()) {
            try {
                Map<ID, T> data = objectMapper.readValue(dbFile, getTypeReference());
                dataBase.putAll(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Files.createFile(dbFile.toPath());
                Files.write(dbFile.toPath(), "{}".getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    protected abstract TypeReference<Map<ID, T>> getTypeReference();

    protected abstract TypeReference<List<T>> getTypeReferenceList();

    @Override
    public synchronized T save(T entity) {
        try {
            String s = objectMapper.writeValueAsString(entity);
            T value = (T) objectMapper.readValue(s, entity.getClass());
            ID id = value.getId();
            dataBase.put(id, value);
            saveDataToFile();
            return findById(id).get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        T value = dataBase.get(id);
        if (value != null) {
            try {
                String s = objectMapper.writeValueAsString(value);
                value = (T) objectMapper.readValue(s, value.getClass());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(value);
    }

    @Override
    public T deleteById(ID id) {
        T remove = dataBase.remove(id);
        saveDataToFile();
        return remove;
    }

    @Override
    public List<T> findAll() {
        ArrayList<T> ts = new ArrayList<>(dataBase.values());
        try {
            String s = objectMapper.writeValueAsString(ts);
            List<T> list = objectMapper.readValue(s, getTypeReferenceList());
            return list;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void saveDataToFile() {
        try {
            objectMapper.writeValue(dbFile, dataBase);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public int deleteAll(List<ID> ids) {
        int count = 0;
        for (ID id : ids) {
            if (dataBase.remove(id) != null) {
                count++;
            }
        }
        saveDataToFile();
        return count;
    }
}
