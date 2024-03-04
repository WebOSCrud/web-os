package cn.donting.web.os.core.service;

import cn.donting.web.os.api.wap.WapInfo;
import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.file.FileWatch;
import cn.donting.web.os.core.file.FileWatchService;
import cn.donting.web.os.core.util.FileUtil;
import cn.donting.web.os.core.wap.Wap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 开发环境自动加载 classpath.dev
 * 并监控 classpath.dev .发生修改时自动 卸载旧的，并重新加载
 * @author donting
 */
@Service
@Profile("dev")
@Slf4j
public class DevWapInstallService implements ApplicationRunner, FileWatch {

    private Map<Path,String> devFileWapIdMap=new ConcurrentHashMap<>();
    final WapService wapManager;

    @Autowired
    WapRuntimeService wapRuntimeService;

    public DevWapInstallService(WapService wapManager) {
        this.wapManager = wapManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String dir = System.getProperty("user.dir");
        File[] files = new File(dir).listFiles();
        for (File file : files) {
            String extName = FileUtil.extName(file);
            if (extName.equals(OsCoreApplication.WPA_DEV_EXT_NAME) && !file.getName().equals("os-core.wev") &&
                    !file.getName().equals("os-wap-launch.wev")
            ) {
                log.info("load dev wap:{}",file.getName());
                WapInfo wapInfo = wapManager.installUpdate(file);
                devFileWapIdMap.put(file.toPath(),wapInfo.getId());
                FileWatchService.FileWatchThread fileWatchThread = FileWatchService.addWatchFile(file, this::watch, StandardWatchEventKinds.ENTRY_MODIFY);
                fileWatchThread.start();
            }
        }
    }

    @Override
    public void watch(File file, WatchEvent.Kind pathKind) {
        String wapId = devFileWapIdMap.get(file.toPath());
        try {
            log.info(file.getPath()+"发生变化，重新加载");
            wapRuntimeService.stop(wapId);
            wapManager.installUpdate(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
