package cn.donting.web.os.core.service;

import cn.donting.web.os.core.db.entity.WapInfo;
import cn.donting.web.os.core.db.repository.IWapInfoRepository;
import cn.donting.web.os.core.exception.WapLoadException;
import cn.donting.web.os.core.exception.WapNotFoundException;
import cn.donting.web.os.core.file.OSFileSpaces;
import cn.donting.web.os.core.loader.WapLoader;
import cn.donting.web.os.core.properties.DevOsProperties;
import cn.donting.web.os.core.security.OsSecurityManager;
import cn.donting.web.os.core.servlet.WapServletConfig;
import cn.donting.web.os.core.servlet.WapServletContext;
import cn.donting.web.os.core.wap.Wap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * wap 运行时 服务类
 * 使用一个 ThreadGroup 来作为 一个 wap 的 ThreadGroup，用于监控 wap内部创建的线程
 * 用于在 停止容器是没有自动结束 wap 内部创建的线程，就可以通过 ThreadGroup 来获取创建的线程来强制结束。使jvm 能够正确的卸载 classloader
 *
 * @author donting
 * @see Thread#group
 * @see Thread#Thread(ThreadGroup, String)
 * @see Thread#Thread(String)
 * @see ThreadGroup
 * @see SecurityManager
 * @see OsSecurityManager
 */
@Service
@Slf4j
public class WapRuntimeService {

    /**
     * wap 超过多少时间没有访问，自动结束
     * 单位秒（s）
     */
    private int wapTimeoutSeconds = 60*15;


    final IWapInfoRepository wapInfoRepository;
    /**
     * wap 运行时集合
     */
    private Map<String, Wap> wapRuntimeMap = new ConcurrentHashMap<>();

    final DevOsProperties devOsProperties;

    @Value("${spring.profiles.active:#{null}}")
    String active;
    public WapRuntimeService(IWapInfoRepository wapInfoRepository, @Autowired(required = false) DevOsProperties devOsProperties) {
        this.wapInfoRepository = wapInfoRepository;
        this.devOsProperties = devOsProperties;
    }

    /**
     * 获取并加载 wap
     *
     * @param wapId
     * @return
     * @throws WapNotFoundException wap未安装
     * @throws Exception            加载异常
     */
    public Wap getAndLoadWap(String wapId) throws WapNotFoundException, WapLoadException {
        Wap wap = wapRuntimeMap.get(wapId);
        if (wap != null) {
            return wap;
        }
        Optional<WapInfo> wapInfoOp = wapInfoRepository.findById(wapId);
        if (wapInfoOp.isPresent()) {
            wap = loadAndStartWap(wapInfoOp.get());
            return wap;
        } else {
            throw new WapNotFoundException(wapId);
        }
    }

    /**
     * 加载并 启动 wap
     *
     * @param wapInfo
     * @return
     * @throws WapLoadException
     */
    private synchronized Wap loadAndStartWap(WapInfo wapInfo) throws WapLoadException {
        if (wapRuntimeMap.containsKey(wapInfo.getId())) {
            return wapRuntimeMap.get(wapInfo.getId());
        }
        String id = wapInfo.getId();
        File file = new File(OSFileSpaces.WAP_DATA, id + File.separator + WapService.jarFileName);
        if (!file.exists()) {
            file = new File(OSFileSpaces.WAP_DATA, id + File.separator + WapService.devFileName);
        }
        WapLoader wapLoader = WapLoader.getWapLoader(file);
        try {
            Wap loader = wapLoader.load();
            wapRuntimeMap.put(id, loader);
            WapServletContext wapServletContext = new WapServletContext(wapInfo.getId());
            WapServletConfig wapServletConfig = new WapServletConfig(loader, wapServletContext);
            ArrayList<String> args=new ArrayList();
            if (devOsProperties != null) {
                if (!ObjectUtils.isEmpty(devOsProperties.getPageNotFoundForwardHost())) {
                    args.add("--"+DevOsProperties.PREFIX+".pageNotFoundForwardHost="+devOsProperties.getPageNotFoundForwardHost());
                    args.add("--"+DevOsProperties.PREFIX+".pageNotFoundForwardWapId="+devOsProperties.isPageNotFoundForwardWapId());
                }
            }
            //主程序进入 哪个active .加载的wap 也进入对应的 active
            if(StringUtils.hasText(active)){
                args.add("--spring.profiles.active="+active);
            }
            loader.start(args.toArray(new String[args.size()]), wapServletContext, wapServletConfig);
            return loader;
        } catch (Exception ex) {
            throw new WapLoadException(id, file, ex);
        }
    }


    public synchronized void stop(String wapId) throws Exception {
        log.info("开始停止wap:{}", wapId);
        Wap wap = wapRuntimeMap.remove(wapId);
        if (wap != null) {
            wap.stop();
            System.gc();
        }
    }

    /**
     * 获取正在运行的 wap
     *
     * @param wapId
     * @return
     */
    public Wap getWap(String wapId) {
        Wap wap = wapRuntimeMap.get(wapId);
        return wap;
    }

    /**
     * 获取所有正在运行的 wap
     *
     * @return
     */
    public List<Wap> getAllRuntime() {
        return new ArrayList<>(wapRuntimeMap.values());
    }

    /**
     * 1分钟检查一次，没有请求的Wap,并结束掉
     */
    @Scheduled(fixedDelay = 1 * 60 * 1000)
    private void checkWapRuntimeTask() {
        Set<String> keySet = wapRuntimeMap.keySet();
        List<String> wapIds = new ArrayList<>();
        for (String wapId : keySet) {
            Wap wap = wapRuntimeMap.get(wapId);
            long httpLastVisitTimme = wap.getHttpLastVisitTimme();
            if (System.currentTimeMillis() - httpLastVisitTimme > wapTimeoutSeconds * 1000) {
                wapIds.add(wapId);
            }
        }
        synchronized (this) {
            for (String wapId : wapIds) {
                try {
                    stop(wapId);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}
