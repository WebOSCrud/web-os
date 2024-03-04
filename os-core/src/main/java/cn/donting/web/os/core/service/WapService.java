package cn.donting.web.os.core.service;

import cn.donting.web.os.api.wap.WapInfo;
import cn.donting.web.os.api.wap.WapWindow;
import cn.donting.web.os.api.wap.WapWindowType;
import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.OsSetting;
import cn.donting.web.os.core.db.entity.FileType;
import cn.donting.web.os.core.db.repository.IOsFileTypeRepository;
import cn.donting.web.os.core.db.repository.IWapInfoRepository;
import cn.donting.web.os.core.exception.CheckException;
import cn.donting.web.os.core.exception.WapLoadException;
import cn.donting.web.os.core.file.OSFileSpaces;
import cn.donting.web.os.core.loader.WapLoader;
import cn.donting.web.os.core.util.BeanCheck;
import cn.donting.web.os.core.util.FileUtil;
import cn.donting.web.os.core.util.ResourceUtil;
import cn.donting.web.os.core.wap.Wap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@Service
public class WapService implements cn.donting.web.os.api.wap.WapService {
    public static final String jarFileName = "wap.jar";
    public static final String devFileName = "wap.wev";


    final IWapInfoRepository wapInfoRepository;
    final IOsFileTypeRepository osFileTypeRepository;
    final OsService osService;
    final WapRuntimeService wapRuntimeService;

    public WapService(IWapInfoRepository wapInfoRepository, IOsFileTypeRepository osFileTypeRepository, OsService osService, WapRuntimeService wapRuntimeService) {
        this.wapInfoRepository = wapInfoRepository;
        this.osFileTypeRepository = osFileTypeRepository;
        this.osService = osService;
        this.wapRuntimeService = wapRuntimeService;
    }

    @Override
    public synchronized WapInfo installUpdate(File file) throws Exception {
        WapLoader wapLoader = WapLoader.getWapLoader(file);
        Wap wap = wapLoader.load();

        checkWapInfo(wap);

        String id = wap.getWapInfo().getId();
        Optional<cn.donting.web.os.core.db.entity.WapInfo> wapInfo = wapInfoRepository.findById(id);
        if (wapInfo.isPresent()) {
            //执行更新
            return update(wap, wapInfo.get(), file);
        }
        //执行第一次安装
        log.info("install wap:" + file);
        File wapDir = new File(OSFileSpaces.WAP_DATA, wap.getWapInfo().getId());
        wapDir.mkdirs();
        String extName = FileUtil.extName(file);
        if (extName.equals(OsCoreApplication.WPA_DEV_EXT_NAME)) {
            Files.copy(file.toPath(), new File(wapDir, devFileName).toPath());
        } else {
            Files.copy(file.toPath(), new File(wapDir, jarFileName).toPath());
        }
        wap.getWapInfo().setInstallTime(System.currentTimeMillis());
        wapInfoRepository.save(wap.getWapInfo());
        updateInfo(wap);
        File wapSpaces = getWapSpaces(wap.getWapInfo().getId());
        wapSpaces.mkdirs();
        return wap.getWapInfo();
    }

    @Override
    public WapInfo getWapInfo(File file) throws Exception {
        WapLoader wapLoader = WapLoader.getWapLoader(file);
        Wap loader = wapLoader.load();
        return loader.getWapInfo();
    }

    /**
     * 检查 Wap 的 WapInfo 安装信息是否符合要求
     * @param wap
     * @throws WapLoadException
     */
    private void checkWapInfo(Wap wap) throws WapLoadException {
        try {
            cn.donting.web.os.core.db.entity.WapInfo wapInfo = wap.getWapInfo();
            List<WapInfo.FileType> fileTypes = wapInfo.getFileTypes();

            BeanCheck.checkNonNull(wapInfo);

            HashSet<WapWindowType> windowTypes = new HashSet<>();
            for (WapWindow wapWindow : wapInfo.getWapWindows()) {
                BeanCheck.checkNonNull(wapWindow);
                WapWindowType type = wapWindow.getType();
                if (type.unique && windowTypes.contains(type)) {
                    throw new WapLoadException(wap.getWapInfo().getId(), wap.getLaodFile(), "WapWindows 类型重复注册：" + type);
                }
                windowTypes.add(type);
            }
            if (fileTypes.size() != 0 && !windowTypes.contains(WapWindowType.OpenFile)) {
                throw new WapLoadException(wap.getWapInfo().getId(), wap.getLaodFile(), "注册了FileTye,但是没有注册 相应打开文件 的 wapWindows,参考 WapWindowType.OpenFile");
            }
        } catch (CheckException ex) {
//            throw new WapLoadException(wap.getWapInfo().getId(),wap.getLaodFile(),"wap.info.json ==>"+ex.getMessage(),ex);
        }
    }


    @Override
    public synchronized boolean uninstall(String wapId) throws Exception {
        Wap wap = wapRuntimeService.getWap(wapId);
        if (wap != null) {
            wapRuntimeService.stop(wapId);
        }
        wapInfoRepository.deleteById(wapId);
        List<FileType> fileTypes = osFileTypeRepository.findByWapId(wapId);
        List<String> ids = new ArrayList<>();
        for (FileType fileType : fileTypes) {
            ids.add(fileType.getId());
        }
        osFileTypeRepository.deleteAll(ids);
        //清除 默认设置
        OsSetting osSetting = osService.getOsSetting();
        if (wapId.equals(osSetting.getDesktopWapId())) {
            osSetting.setDesktopWapId(null);
            osSetting.setLoginIgnoreRURL(null);
        }
        if (wapId.equals(osSetting.getFileManagerSelectWapId())) {
            osSetting.setFileManagerSelectWapId(null);
        }
        osService.saveOsSetting(osSetting);

        //删除相关资源
        deleteWapInfo(wapId);
        File wapDir = new File(OSFileSpaces.WAP_DATA, wapId);
        try {
            FileUtil.deleteFile(wapDir);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        return true;
    }


    /**
     * wpa 更新
     *
     * @param wap
     * @param file 更新文件
     * @return
     * @throws Exception
     */
    private WapInfo update(Wap wap, WapInfo oldWpaInfo, File file) throws Exception {
        int numberVersion = oldWpaInfo.getNumberVersion();
        log.info("update wap:{},file:{}", wap.getWapInfo().getId(), file);
        File wapDir = new File(OSFileSpaces.WAP_DATA, wap.getWapInfo().getId());
        String extName = FileUtil.extName(file);
        if (extName.equals(OsCoreApplication.WPA_DEV_EXT_NAME)) {
            Files.copy(file.toPath(), new File(wapDir, devFileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            //检查更新版本
            if (wap.getWapInfo().getNumberVersion() <= numberVersion) {
                throw new WapLoadException(wap.getWapInfo().getId(), file, "更新版本低于安装版本，无法更新");
            }
            Files.copy(file.toPath(), new File(wapDir, jarFileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        wap.getWapInfo().setUpdateTime(System.currentTimeMillis());
        wapInfoRepository.save(wap.getWapInfo());
        updateInfo(wap);
        return wap.getWapInfo();
    }

    /**
     * 获取安装列表
     *
     * @return
     */
    @Override
    public List<WapInfo> getInstallList() {
        List<cn.donting.web.os.core.db.entity.WapInfo> all = wapInfoRepository.findAll();
        ArrayList<WapInfo> wapInfos = new ArrayList<>(all);
        return wapInfos;
    }

    /**
     * 获取 wap 数据空间
     *
     * @param wapId
     * @return
     */
    @Override
    public File getWapSpaces(String wapId) {
        File wapData = OSFileSpaces.WAP_DATA;
        File file = new File(wapData, wapId + File.separator + OSFileSpaces.WAP_DATA_NAME);
        return file;
    }

    /**
     * 更新文件类型
     *
     * @param wap
     */
    private void updateFileType(Wap wap) {
        log.info("updateFileType:{}", wap.getWapInfo().getId());
        deleteWapInfo(wap.getWapInfo().getId());
        //在执行新的文件类型设置
        installFileType(wap);
    }

    /**
     * 首次安装 时，设置文件类型
     *
     * @param wap
     */
    private void installFileType(Wap wap) {
        List<WapInfo.FileType> fileTypes = wap.getWapInfo().getFileTypes();
        HashSet<WapURL> resources = new HashSet<>();
        for (WapInfo.FileType fileType : fileTypes) {
            //不存在默认 文件则自动 设置为默认
            if (!osFileTypeRepository.findById(fileType.getExtName()).isPresent()) {
                FileType defFileType = new FileType();
                defFileType.setExtName(fileType.getExtName());
                defFileType.setWapId(wap.getWapInfo().getId());
                defFileType.setIconResource(fileType.getIconResource());
                defFileType.setDescription(fileType.getDescription());
                osFileTypeRepository.save(defFileType);
            }
            URL resource = ResourceUtil.getWapResourceURL(wap, fileType.getIconResource());
            if (resource != null) {
                resources.add(new WapURL(fileType.getIconResource(), resource));
            }
        }
        List<WapWindow> wapWindows = wap.getWapInfo().getWapWindows();
        for (WapWindow wapWindow : wapWindows) {
            URL resource = ResourceUtil.getWapResourceURL(wap, wapWindow.getIconResource());
            if (resource != null) {
                resources.add(new WapURL(wapWindow.getIconResource(), resource));
            }
        }
        URL resource = ResourceUtil.getWapResourceURL(wap, wap.getWapInfo().getIconResource());
        if (resource != null) {
            resources.add(new WapURL(wap.getWapInfo().getIconResource(), resource));
        }
    }

    private synchronized void updateInfo(Wap wap) {
        updateFileType(wap);
        OsSetting osSetting = osService.getOsSetting();
        String desktopWapId = osSetting.getDesktopWapId();
        for (WapWindow wapWindow : wap.getWapInfo().getWapWindows()) {
            if (desktopWapId == null && wapWindow.getType().equals(WapWindowType.Desktop)) {
                osSetting.setDesktopWapId(wap.getWapInfo().getId());
                List<String> ignoreURLs = wapWindow.getLoginIgnoreURL();
                List<String> loginIgnoreURL = new ArrayList<>(ignoreURLs.size());
                for (String ignoreURL : ignoreURLs) {
                    //不以/ 开头加上 /
                    if (!ignoreURL.startsWith("/")) {
                        ignoreURL = "/" + ignoreURL;
                    }
                    loginIgnoreURL.add(ignoreURL);
                }
                osSetting.setLoginIgnoreRURL(loginIgnoreURL);

            }
            if (osSetting.getFileManagerSelectWapId() == null && wapWindow.getType().equals(WapWindowType.FileManagerSelect)) {
                osSetting.setFileManagerSelectWapId(wap.getWapInfo().getId());
            }
        }
        try {
            osService.saveOsSetting(osSetting);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }


    private void deleteWapInfo(String wapId) {
        List<FileType> fileTypes = osFileTypeRepository.findByWapId(wapId);
        if (fileTypes.size() > 0) {
            List<String> ids = new ArrayList<>();
            fileTypes.forEach(fileType -> ids.add(fileType.getId()));
            //先删除旧的 文件类型
            osFileTypeRepository.deleteAll(ids);
        }
    }

    private static class WapURL {
        private final String path;
        private final URL url;

        public WapURL(String path, URL url1) {
            this.path = path;
            this.url = url1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WapURL wapURL = (WapURL) o;
            return Objects.equals(path, wapURL.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path);
        }
    }

}
