package cn.donting.web.os.core.service;

import cn.donting.web.os.api.wap.*;
import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.OsSetting;
import cn.donting.web.os.core.db.entity.OsFileType;
import cn.donting.web.os.core.db.entity.WapResource;
import cn.donting.web.os.core.db.repository.IOsFileTypeRepository;
import cn.donting.web.os.core.db.repository.IWapInstallInfoRepository;
import cn.donting.web.os.core.db.repository.IWapResourceRepository;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class WapService implements cn.donting.web.os.api.wap.WapService {
    public static final String jarFileName = "wap.jar";
    public static final String devFileName = "wap.wev";


    final IWapInstallInfoRepository wapInstallInfoRepository;
    final IWapResourceRepository wapResourceRepository;
    final IOsFileTypeRepository osFileTypeRepository;
    final OsService osService;
    final WapRuntimeService wapRuntimeService;

    public WapService(IWapInstallInfoRepository wapInfoRepository,
                      IWapResourceRepository wapResourceRepository, IOsFileTypeRepository osFileTypeRepository,
                      OsService osService,
                      WapRuntimeService wapRuntimeService) {
        this.wapInstallInfoRepository = wapInfoRepository;
        this.wapResourceRepository = wapResourceRepository;
        this.osFileTypeRepository = osFileTypeRepository;
        this.osService = osService;
        this.wapRuntimeService = wapRuntimeService;
    }

    @Override
    public synchronized WapInstallInfo installUpdate(File file) throws Exception {
        log.info("install file:{}",file);
        WapLoader wapLoader = WapLoader.getWapLoader(file);
        Wap wap = wapLoader.load();
        checkWap(wap);
        log.info("install wapId:{}",wap.getWapInfo().getId());

        File wapDir = new File(OSFileSpaces.WAP_DATA, wap.getWapInfo().getId());
        wapDir.mkdirs();
        String extName = FileUtil.extName(file);
        //复制 jar 文件
        if (extName.equals(OsCoreApplication.WPA_DEV_EXT_NAME)) {
            Files.copy(file.toPath(), new File(wapDir, devFileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.copy(file.toPath(), new File(wapDir, jarFileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        Optional<WapInstallInfo> wapInstallInfoOp = wapInstallInfoRepository.findById(wap.getWapInfo().getId());
        WapInstallInfo newWapInstallInfo;
        //已经安装过，执行更新
        if (wapInstallInfoOp.isPresent()) {
            log.info("update wap:{}",wap.getWapInfo().getId());
            WapInstallInfo oldWapInstallInfo = wapInstallInfoOp.get();
            newWapInstallInfo = new WapInstallInfo();
            newWapInstallInfo.setWapInfo(wap.getWapInfo());
            newWapInstallInfo.setInstallTime(oldWapInstallInfo.getInstallTime());
            newWapInstallInfo.setUpdateTime(System.currentTimeMillis());
            wapInstallInfoRepository.save(newWapInstallInfo);

        } else {
            log.info("first install wap:{}",wap.getWapInfo().getId());
            //首次安装
            newWapInstallInfo = new WapInstallInfo();
            newWapInstallInfo.setWapInfo(wap.getWapInfo());
            newWapInstallInfo.setInstallTime(System.currentTimeMillis());
            newWapInstallInfo.setUpdateTime(0);
            wapInstallInfoRepository.save(newWapInstallInfo);

        }
        String wapId = wap.getWapInfo().getId();
        deleteWapResource(wapId);
        if (!new File(OSFileSpaces.OS_WAP_RESOURCES, wapId).mkdirs()) {
            throw new WapLoadException(wapId,file,"wapId 出现特殊字符无法创建文件夹");
        }
        deleteOsFileType(wapId);

        updateWapWindow(wap);
        updateWapFileType(wap);

        return newWapInstallInfo;


    }

    private void deleteOsFileType(String wapId) {
        List<OsFileType> osFileTypes = osFileTypeRepository.findByWapId(wapId);
        List<String> ids = osFileTypes.stream().map(OsFileType::getExtName).collect(Collectors.toList());
        osFileTypeRepository.deleteAll(ids);
    }


    private void deleteWapResource(String wapId) {
        List<WapResource> wapResources = wapResourceRepository.findByWapId(wapId);
        List<String> ids = wapResources.stream().map(WapResource::getId).collect(Collectors.toList());
        wapResourceRepository.deleteAll(ids);
        File file = new File(OSFileSpaces.OS_WAP_RESOURCES, wapId);
        try {
            FileUtil.deleteFile(file);
        } catch (IOException e) {
            log.warn(e.getMessage(),e);
        }
    }


    private void updateWapWindow(Wap wap) throws IOException {
        WapInfo wapInfo = wap.getWapInfo();
        List<WapWindow> wapWindows = wapInfo.getWapWindows();
        List<WapResource> newWapResource = new ArrayList<>();
        for (WapWindow wapWindow : wapWindows) {
            String iconResource = wapWindow.getIconResource();
            WapResource wapResource = copyWapResource(iconResource, wap);
            if (wapResource != null) {
                newWapResource.add(wapResource);
            }
            if (wapWindow.getType().equals(WapWindowType.Desktop)) {
                OsSetting osSetting = osService.getOsSetting();
                if (osSetting.getDesktopWapId()==null) {
                    osSetting.setDesktopWapId(wapInfo.getId());
                    osService.saveOsSetting(osSetting);
                }
            }
        }
        wapResourceRepository.saveAll(newWapResource);
    }

    private void updateWapFileType(Wap wap) throws IOException {
        WapInfo wapInfo = wap.getWapInfo();
        List<WapResource> newWapResource = new ArrayList<>();
        Set<String> wapResourceId=new HashSet<>();
        List<FileType> fileTypes = wapInfo.getFileTypes();
        List<OsFileType> osFileTypes = new ArrayList<>();
        for (FileType fileType : fileTypes) {
            String iconResource = fileType.getIconResource();
            WapResource wapResource = copyWapResource(iconResource, wap);
            if (wapResource != null && !wapResourceId.contains(wapResource.getId())) {
                newWapResource.add(wapResource);
                wapResourceId.add(wapResource.getId());
            }
            Optional<OsFileType> osFileTypeOp = osFileTypeRepository.findById(fileType.getExtName());
            if (!osFileTypeOp.isPresent()) {
                OsFileType osFileType = new OsFileType();
                osFileType.setExtName(fileType.getExtName());
                osFileType.setDescription(fileType.getDescription());
                osFileType.setWapId(wapInfo.getId());
                osFileType.setIconResource(fileType.getIconResource());
                osFileTypes.add(osFileType);
            }
        }
        wapResourceRepository.saveAll(newWapResource);
        osFileTypeRepository.saveAll(osFileTypes);
    }


    private WapResource copyWapResource(String resource, Wap wap) throws IOException {
        File resourcesDir = new File(OSFileSpaces.OS_WAP_RESOURCES, wap.getWapInfo().getId());
        if (resource == null) {
            return null;
        }
        URL resourceURL = wap.getWapClassLoader().getResource(resource);
        if (resource == null) {
            log.warn(resourceURL + " is null");
            return null;
        }
        WapInfo wapInfo = wap.getWapInfo();
        String wapResourceId = ResourceUtil.getWapResourceId(wapInfo.getId(), resource);
        log.info("copy wapResourceId:{}",wapResourceId);
        if (wapResourceRepository.findById(wapResourceId).isPresent()) {
            return null;
        }
        WapResource wapResource = new WapResource();
        wapResource.setWapId(wapInfo.getId());
        wapResource.setId(wapResourceId);
        wapResource.setFileName(UUID.randomUUID() + "." + FileUtil.extName(resource));
        FileUtil.copyFile(resourceURL, new File(resourcesDir, wapResource.getFileName()));
        return wapResource;
    }

    @Override
    public WapInfo getWapInfo(File file) throws Exception {
        WapLoader wapLoader = WapLoader.getWapLoader(file);
        Wap loader = wapLoader.load();
        return loader.getWapInfo();
    }

    /**
     * 检查 Wap 的 WapInfo 安装信息是否符合要求
     *
     * @param wap
     * @throws WapLoadException
     */
    private void checkWap(Wap wap) throws WapLoadException {
        WapInfo wapInfo = wap.getWapInfo();
        try {
            BeanCheck.checkNonNull(wapInfo);
            for (WapWindow wapWindow : wapInfo.getWapWindows()) {
                BeanCheck.checkNonNull(wapWindow);
            }
        } catch (Exception ex) {
            throw new WapLoadException(wapInfo.getId(), wap.getLaodFile(), ex.getMessage());
        }
    }


    @Override
    public synchronized boolean uninstall(String wapId) throws Exception {
        wapRuntimeService.stop(wapId);
        deleteOsFileType(wapId);
        //TODO:回复已有的其他 wap 注册的 fileType为默认
        return true;
    }

    /**
     * 获取安装列表
     *
     * @return
     */
    @Override
    public List<WapInstallInfo> getInstallList() {
        return wapInstallInfoRepository.findAll();
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


}
