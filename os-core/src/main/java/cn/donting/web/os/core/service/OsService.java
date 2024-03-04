package cn.donting.web.os.core.service;

import cn.donting.web.os.core.OsSetting;
import cn.donting.web.os.core.file.OSFileSpaces;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
@Slf4j
@Service
public class OsService {
    private final File osSettingFile = new File(OSFileSpaces.OS, "os.setting.json");

    private  final ObjectMapper objectMapper;
    private OsSetting osSetting;

    public OsService(ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
        if (osSettingFile.exists()) {
            osSetting = objectMapper.readValue(osSettingFile, OsSetting.class);
        } else {
            osSetting = new OsSetting();
            objectMapper.writeValue(osSettingFile, osSetting);
        }
    }

    public OsSetting getOsSetting()  {
        return osSetting;
    }

    public synchronized void saveOsSetting(OsSetting osSetting) throws IOException {
        log.info("saveOsSetting:{}",osSetting);
        objectMapper.writeValue(osSettingFile, osSetting);
        this.osSetting=osSetting;
    }
}

