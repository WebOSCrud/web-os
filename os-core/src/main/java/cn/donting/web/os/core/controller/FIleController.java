package cn.donting.web.os.core.controller;

import cn.donting.web.os.api.wap.*;
import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.db.entity.OsFileType;
import cn.donting.web.os.core.db.entity.WapResource;
import cn.donting.web.os.core.db.repository.IOsFileTypeRepository;
import cn.donting.web.os.core.db.repository.IWapInstallInfoRepository;
import cn.donting.web.os.core.db.repository.IWapResourceRepository;
import cn.donting.web.os.core.domain.param.FileOpenPar;
import cn.donting.web.os.core.domain.param.FilePathPar;
import cn.donting.web.os.core.file.OSFileSpaces;
import cn.donting.web.os.core.service.OsService;
import cn.donting.web.os.core.util.FileUtil;
import cn.donting.web.os.core.util.ResourceUtil;
import cn.donting.web.os.core.vo.*;
import cn.donting.web.os.core.vo.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSInput;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/file")
public class FIleController {

    final IOsFileTypeRepository osFileTypeRepository;
    final IWapInstallInfoRepository wapInstallInfoRepository;
    final IWapResourceRepository wapResourceRepository;
    final OsService osService;

    private Set<String> imgExtName;

    public FIleController(IOsFileTypeRepository osFileTypeRepository, IWapInstallInfoRepository wapInfoRepository, IWapResourceRepository wapResourceRepository, OsService osService) {
        this.osFileTypeRepository = osFileTypeRepository;
        this.wapInstallInfoRepository = wapInfoRepository;
        this.wapResourceRepository = wapResourceRepository;
        this.osService = osService;
        imgExtName = Arrays.asList("png", "jpg", "jpeg", "svg").stream().collect(Collectors.toSet());
    }

    /**
     * 获取文件图标
     *
     * @param path 绝对路径
     */
    @GetMapping("/icon")
    public void fileIcon(String path, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            httpServletResponse.setStatus(404);
            return;
        }
        //文件扩展名
        String extName = FileUtil.extName(file).toLowerCase();
        //是图片直接返回图片
        if (imgExtName.contains(extName)) {
            String contentType = FileUtil.getImageContentType("name." + extName);
            httpServletResponse.setContentType(contentType);
            httpServletResponse.getOutputStream().write(Files.readAllBytes(file.toPath()));
            return;
        }
        //可以让前端使用缓存
        httpServletResponse.sendRedirect("/" + OsCoreApplication.OS_ID + "/file/ext_name/icon?extName=" + extName);
    }

    @GetMapping("/ext_name/icon")
    public void fileExtIcon(String extName, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        //文件扩展名
        extName = extName.toLowerCase();
        if (extName.equals(OsCoreApplication.WPA_EXT_NAME) || extName.equals(OsCoreApplication.WPA_DEV_EXT_NAME)) {
            httpServletRequest.getRequestDispatcher("/" + OsCoreApplication.OS_ID + "/img/wap-file.png").forward(httpServletRequest, httpServletResponse);
        } else {
            //
            Optional<OsFileType> fileTypeOp = osFileTypeRepository.findById(extName);
            if (!fileTypeOp.isPresent()) {
                httpServletRequest.getRequestDispatcher("/" + OsCoreApplication.OS_ID + "/img/unknown-file.png").forward(httpServletRequest, httpServletResponse);
                return;
            }
            OsFileType fileType = fileTypeOp.get();
            String wapId = fileType.getWapId();
            String wapResourceId = ResourceUtil.getWapResourceId(wapId, fileType.getIconResource());
            Optional<WapResource> wapResourceOp = wapResourceRepository.findById(wapResourceId);
            if (!wapResourceOp.isPresent()) {
                httpServletRequest.getRequestDispatcher("/" + OsCoreApplication.OS_ID + "/img/unknown-file.png").forward(httpServletRequest, httpServletResponse);
                return;
            }
            WapResource wapResource = wapResourceOp.get();
            String fileName = wapResource.getFileName();

            File wapResourcesDir = new File(OSFileSpaces.OS_WAP_RESOURCES, wapId);
            File imgFile = new File(wapResourcesDir, wapResource.getFileName());

            String contentType = FileUtil.getImageContentType(fileName);
            httpServletResponse.setContentType(contentType);
            httpServletResponse.getOutputStream().write(Files.readAllBytes(imgFile.toPath()));
            return;
        }
    }

    /**
     * 获取文件打开的窗口参数
     */
    @PostMapping("/open")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseBody<WapWindowOption> fileOpen(@RequestBody FileOpenPar fileOpenPar) {
        String extName = FileUtil.extName(new File(fileOpenPar.getFlePath()));
        String wapId = fileOpenPar.getWapId();

        if (fileOpenPar.getWapId() == null) {
            Optional<OsFileType> osFileTypeOptional = osFileTypeRepository.findById(extName);
            if (osFileTypeOptional.isPresent()) {
                wapId = osFileTypeOptional.get().getWapId();
                fileOpenPar.setWapId(wapId);
            }
        }
        //没有默认打开的 wap
        if (wapId == null) {
            return ResponseBody.fail(ResponseBodyCodeEnum.FILE_OPEN_FAIL);
        }

        WapInstallInfo wapInstallInfo = wapInstallInfoRepository.findById(fileOpenPar.getWapId()).get();
        for (WapWindow wapWindow : wapInstallInfo.getWapInfo().getWapWindows()) {
            if (wapWindow.getType().equals(WapWindowType.OpenFile)) {
                if (fileOpenPar.isDef()) {
                    OsFileType osFileType = new OsFileType();
                    osFileType.setIconResource(wapWindow.getIconResource());
                    osFileType.setExtName(extName);
                    osFileType.setDescription(wapWindow.getDescription());
                    osFileType.setWapId(wapInstallInfo.getWapInfo().getId());
                    log.info("设置 {} 打开方式为 {}", extName, wapInstallInfo.getWapInfo().getId());
                    osFileTypeRepository.save(osFileType);
                }
                return ResponseBody.success(wapWindow.getOption());
            }
        }

        return ResponseBody.fail(ResponseBodyCodeEnum.FILE_OPEN_FAIL);
    }


    /**
     * 获取能打开文件的 wap 列表
     */
    @PostMapping("/open/wap/list")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseBody<FileOpenWapInfoListVo> fileOpenList(@RequestBody FilePathPar filePathPar) {
        String extName = FileUtil.extName(new File(filePathPar.getFlePath()));
        FileOpenWapInfoListVo fileOpenWapInfoListVo = new FileOpenWapInfoListVo();
        List<WapInstallInfo> wapInstallInfos = wapInstallInfoRepository.findAll();
        for (WapInstallInfo wapInstallInfo : wapInstallInfos) {
            WapInfo wapInfo = wapInstallInfo.getWapInfo();
            boolean other = true;
            for (FileType fileType : wapInfo.getFileTypes()) {
                if (fileType.getExtName().equalsIgnoreCase(extName)) {
                    Optional<WapResource> wapResourceOp = wapResourceRepository.findById(ResourceUtil.getWapResourceId(wapInfo.getId(), fileType.getIconResource()));
                    WapBaseInfoVo wapBaseInfoVo = new WapBaseInfoVo();
                    wapBaseInfoVo.setWapId(wapInfo.getId());
                    wapBaseInfoVo.setName(wapInfo.getName());
                    wapBaseInfoVo.setIconUrl(ResourceUtil.getWapResourceHttpURL(wapResourceOp.orElse(null)));
                    fileOpenWapInfoListVo.getNormal().add(wapBaseInfoVo);
                    other = false;
                    break;
                }
            }
            if (other) {
                for (WapWindow wapWindow : wapInfo.getWapWindows()) {
                    if (wapWindow.getType().equals(WapWindowType.OpenFile)) {
                        Optional<WapResource> wapResourceOp = wapResourceRepository.findById(ResourceUtil.getWapResourceId(wapInfo.getId(),
                                wapWindow.getIconResource()));
                        WapBaseInfoVo wapBaseInfoVo = new WapBaseInfoVo();
                        wapBaseInfoVo.setWapId(wapInfo.getId());
                        wapBaseInfoVo.setName(wapInfo.getName());
                        wapBaseInfoVo.setIconUrl(ResourceUtil.getWapResourceHttpURL(wapResourceOp.orElse(null)));
                        fileOpenWapInfoListVo.getOthers().add(wapBaseInfoVo);
                        break;
                    }
                }
            }
        }
        return ResponseBody.success();
    }

    /**
     * 获取系统注册的 文件类型
     */
    @GetMapping("/types")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseBody<List<FileTypeVo>> getFileTypes() {
        List<OsFileType> fileTypes = osFileTypeRepository.findAll();
        List<WapInstallInfo> wapInstallInfos = wapInstallInfoRepository.findAll();
        HashMap<String, WapWindow> wapWindowMap = new HashMap<>();
        for (WapInstallInfo wapInstallInfo : wapInstallInfos) {
            for (WapWindow wapWindow : wapInstallInfo.getWapInfo().getWapWindows()) {
                if (wapWindow.getType().equals(WapWindowType.OpenFile)) {
                    wapWindowMap.put(wapInstallInfo.getWapInfo().getId(), wapWindow);
                    break;
                }
            }
        }

        AtomicBoolean unknown = new AtomicBoolean(true);
        List<FileTypeVo> collect = fileTypes.stream().map((fileType -> {
            Optional<WapResource> wapResourceOp = wapResourceRepository.findById(ResourceUtil.getWapResourceId(fileType.getWapId(),
                    fileType.getIconResource()));
            if (fileType.getExtName().equalsIgnoreCase(FileType.ext_name_unknown)) {
                unknown.set(false);
            }
            FileTypeVo fileTypeVo = new FileTypeVo();
            fileTypeVo.setDescription(fileType.getDescription());
            fileTypeVo.setWapId(fileType.getWapId());
            fileTypeVo.setIconUrl(ResourceUtil.getWapResourceHttpURL(wapResourceOp.orElseGet(null)));
            fileTypeVo.setExtName(fileType.getExtName().toLowerCase());

            WapWindow wapWindow = wapWindowMap.get(fileType.getWapId());
            if (wapWindow != null) {
                wapResourceOp = wapResourceRepository.findById(
                        ResourceUtil.getWapResourceId(
                                fileType.getWapId(),
                                wapWindow.getIconResource())
                );
                if (wapResourceOp.isPresent()) {
                    WapResource wapResource = wapResourceOp.get();
                    fileTypeVo.setOpenWindowIcon(ResourceUtil.getWapResourceHttpURL(wapResource));
                }
            }


            return fileTypeVo;
        })).collect(Collectors.toList());
        if (unknown.get()) {
            FileTypeVo fileTypeVo = new FileTypeVo();
            fileTypeVo.setDescription("未知文件");
            fileTypeVo.setWapId(OsCoreApplication.OS_ID);
            fileTypeVo.setIconUrl("/" + OsCoreApplication.OS_ID + "/img/unknown-file.png");
            fileTypeVo.setExtName(FileType.ext_name_unknown);
        }
        return ResponseBody.success(collect);
    }


//    /**
//     * 获取文件选择器的 窗口 参数
//     */
//    @GetMapping("/select/window/option")
//    @org.springframework.web.bind.annotation.ResponseBody
//    public ResponseBody<WapWindowOptionVo> selectFileWindow() throws IOException {
//        OsSetting osSetting = osService.getOsSetting();
//        String fileManagerSelectWapId = osSetting.getFileManagerSelectWapId();
//        if (fileManagerSelectWapId != null) {
//            WapInfo wapInfo = wapInfoRepository.findById(fileManagerSelectWapId).get();
//            List<WapWindow> wapWindows = wapInfo.getWapWindows();
//            for (WapWindow wapWindow : wapWindows) {
//                if (wapWindow.getType().equals(WapWindowType.FileManagerSelect)) {
//                    WapWindowOptionVo wapWindowOptionVo = new WapWindowOptionVo(wapWindow, wapInfo.getId());
//                    return ResponseBody.success(wapWindowOptionVo);
//                }
//            }
//        }
//        return ResponseBody.fail(ResponseBodyCodeEnum.NOT_FOUND, "无法选择文件。没有安装对应的wap");
//    }
}
