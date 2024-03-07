package cn.donting.web.os.core;

import cn.donting.web.os.core.file.OSFileSpaces;
import cn.donting.web.os.core.security.OsSecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


@SpringBootApplication
@Slf4j
@EnableScheduling
public class OsCoreApplication implements InitializingBean {
    public static final String OS_ID = "os";
    public static final String WPA_EXT_NAME = "wap";
    public static final String WPA_DEV_EXT_NAME = "wev";
    public static final String OS_VERSION = "0.0.1";
    //TODO
//    final IOsFileTypeRepository fileTypeRepository;

//    public OsCoreApplication(IOsFileTypeRepository fileTypeRepository) {
//        this.fileTypeRepository = fileTypeRepository;
//    }
    public OsCoreApplication() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(OSFileSpaces.OS, "os.json");
        if (!file.exists()) {
            //首次运行
            firstInstall();
            return;
        }
        OsInfo osInfo = objectMapper.readValue(file, OsInfo.class);
        if (OS_VERSION.equals(osInfo.getVersion())) {
            return;
        }
        //OS 版本变化更新了
        update();
    }

    private void firstInstall() throws IOException {
        log.info("firstInstall os");
        updateOSInfo();
    }

    private void updateOSInfo() throws IOException {
        OsInfo osInfo = new OsInfo();
        osInfo.setVersion(OS_VERSION);
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(OSFileSpaces.OS, "os.json");
        objectMapper.writeValue(file, osInfo);
    }

    private void update() throws IOException {
        log.info("update os");
        //TODO
//        Optional<FileType> fileTypeOp = fileTypeRepository.findById(WPA_EXT_NAME);
//        if (fileTypeOp.isPresent()) {
//            if (fileTypeOp.get().getWapId().equals(OS_ID)) {
//                updateOSInfo();
//            }
//        }
    }


    public static void main(String[] args) throws Exception {

        OsSecurityManager securityManager = new OsSecurityManager();
        System.setSecurityManager(securityManager);
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = OsCoreApplication.class.getClassLoader();
        log.info("classLoader:{}", classLoader);
        log.info("contextClassLoader:{}", contextClassLoader);
        SpringApplication.run(OsCoreApplication.class, args);
    }
}
