package cn.donting.web.os.core.controller;

import cn.donting.web.os.api.wap.WapWindow;
import cn.donting.web.os.api.wap.WapWindowType;
import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.OsSetting;
import cn.donting.web.os.core.db.entity.FileType;
import cn.donting.web.os.core.db.entity.WapInfo;
import cn.donting.web.os.core.db.repository.IOsFileTypeRepository;
import cn.donting.web.os.core.db.repository.IWapInfoRepository;
import cn.donting.web.os.core.file.OSFileSpaces;
import cn.donting.web.os.core.loader.WapLoader;
import cn.donting.web.os.core.service.OsService;
import cn.donting.web.os.core.util.FileUtil;
import cn.donting.web.os.core.util.ResourceUtil;
import cn.donting.web.os.core.vo.*;
import cn.donting.web.os.core.vo.ResponseBody;
import cn.donting.web.os.core.wap.Wap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/file")
public class FIleController {


    final IOsFileTypeRepository osFileTypeRepository;
    final IWapInfoRepository wapInfoRepository;
    final OsService osService;

    private Set<String> imgExtName;

    public FIleController(IOsFileTypeRepository osFileTypeRepository, IWapInfoRepository wapInfoRepository, OsService osService) {
        this.osFileTypeRepository = osFileTypeRepository;
        this.wapInfoRepository = wapInfoRepository;
        this.osService = osService;
        imgExtName = Arrays.asList("png", "jpg", "jpeg", "svg").stream().collect(Collectors.toSet());
    }

    /**
     * 获取文件图标
     *
     * @param path 绝对路径
     */
    @GetMapping("/icon")
    public ResponseEntity fileIcon(String path, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        //文件扩展名
        String extName = FileUtil.extName(file).toLowerCase();
        File wapFile;
        String iconResource = null;
        if (imgExtName.contains(extName)) {
            httpServletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
            try (InputStream in = new FileInputStream(file);
                 OutputStream out = httpServletResponse.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return null;
        }

        if (extName.equals(OsCoreApplication.WPA_EXT_NAME) || extName.equals(OsCoreApplication.WPA_DEV_EXT_NAME)) {
            wapFile = file;
        } else {
            Optional<FileType> fileTypeOp = osFileTypeRepository.findById(extName);
            if (!fileTypeOp.isPresent()) {
                httpServletRequest.getRequestDispatcher("/" + OsCoreApplication.OS_ID + "/img/unknown-file.png").forward(httpServletRequest, httpServletResponse);
                return null;
            }
            FileType fileType = fileTypeOp.get();
            String wapId = fileType.getWapId();
            wapFile = OSFileSpaces.getWapInstallFile(wapId);
            iconResource = fileType.getIconResource();
        }
        try {
            Wap wap = WapLoader.getWapLoader(wapFile).load();
            WapInfo wapInfo = wap.getWapInfo();
            if (iconResource == null) {
                iconResource = wapInfo.getIconResource();
            }
            URL resource = ResourceUtil.getWapResourceURL(wap, iconResource);
            if (resource == null) {
                if (wapFile == file) {
                    httpServletRequest.getRequestDispatcher("/" + OsCoreApplication.OS_ID + "/img/wap-file.png").forward(httpServletRequest, httpServletResponse);
                } else {
                    httpServletRequest.getRequestDispatcher("/" + OsCoreApplication.OS_ID + "/img/unknown-file.png").forward(httpServletRequest, httpServletResponse);
                }
                return ResponseEntity.notFound().build();
            }
            MediaType imageMediaType = FileUtil.getImageMediaType(iconResource);
            URLConnection connection = resource.openConnection();
            InputStream inputStream = connection.getInputStream();
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            return ResponseEntity.ok().contentType(imageMediaType).body(inputStreamResource);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 获取文件打开的窗口参数
     *
     * @param path   绝对路径
     * @param wapId  使用 指定wap 打开, 通过 {@link FIleController#fileOpenList(String)}可以拿到 可以打开文件的 wap 列表
     * @param setDef setDef 是否设置为默认打开方式
     * @see FIleController#fileOpenList(String)
     */
    @GetMapping("/open")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseBody<WapWindowOptionVo> fileOpen(String path,
                                                    @RequestParam(required = false) String wapId,
                                                    @RequestParam(required = false, defaultValue = "false") boolean setDef) {

        File file = new File(path);
        if (!file.exists()) {
            return ResponseBody.fail(ResponseBodyCodeEnum.FILE_OPEN_FAIL);
        }
        String extName = FileUtil.extName(file);
        WapInfo wapInfo;
        if (wapId != null) {
            wapInfo = wapInfoRepository.findById(wapId).get();
        } else {
            Optional<FileType> fileTypeOp = osFileTypeRepository.findById(extName);
            if (!fileTypeOp.isPresent()) {
                return ResponseBody.fail(ResponseBodyCodeEnum.FILE_OPEN_FAIL);
            }
            FileType fileType = fileTypeOp.get();
            wapInfo = wapInfoRepository.findById(fileType.getWapId()).get();
        }
        for (WapWindow wapWindow : wapInfo.getWapWindows()) {
            if (wapWindow.getType().equals(WapWindowType.OpenFile)) {
                WapWindowOptionVo wapWindowOptionVo = new WapWindowOptionVo(wapWindow, wapInfo.getId());
                //是否有FileType 信息
                List<cn.donting.web.os.api.wap.WapInfo.FileType> fileTypes = wapInfo.getFileTypes();
                cn.donting.web.os.api.wap.WapInfo.FileType fileType = null;
                for (cn.donting.web.os.api.wap.WapInfo.FileType ft : fileTypes) {
                    if (ft.getExtName().equals(extName)) {
                        fileType = ft;
                    }
                }
                //设置为默认 打开文件的方式
                if (wapId != null && !wapId.equals(wapInfo.getId()) && setDef) {
                    FileType reFileType = new FileType();
                    if (fileTypes != null) {
                        reFileType.setDescription(fileType.getDescription());
                        reFileType.setIconResource(fileType.getIconResource());
                    } else {
                        //没有 fileType 信息，则 图标是 wapInfo 的图标
                        reFileType.setDescription(extName + "文件");
                        reFileType.setIconResource(wapInfo.getIconResource());
                    }
                    reFileType.setWapId(wapId);
                    reFileType.setExtName(extName);
                    osFileTypeRepository.save(reFileType);
                }
                return ResponseBody.success(wapWindowOptionVo);
            }
        }
        log.warn("有fileType,但是没有WapWindow");
        return ResponseBody.fail(ResponseBodyCodeEnum.FILE_OPEN_FAIL);
    }


    /**
     * 获取能打开文件的 wap 列表
     *
     * @param path 绝对路径
     */
    @GetMapping("/open/wap/list")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseBody<FileOpenWapInfoListVo> fileOpenList(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return ResponseBody.fail(ResponseBodyCodeEnum.FILE_OPEN_FAIL);
        }
        String extName = FileUtil.extName(file);
        FileOpenWapInfoListVo fileOpenWapInfoListVo = new FileOpenWapInfoListVo();

        List<WapInfo> wapInfos = wapInfoRepository.findAll();
        for (WapInfo wapInfo : wapInfos) {
            List<WapInfo.FileType> fileTypes = wapInfo.getFileTypes();
            boolean normal = false;
            for (WapInfo.FileType fileType : fileTypes) {
                if (fileType.getExtName().equals(extName)) {
                    fileOpenWapInfoListVo.getNormal().add(new WapBaseInfoVo(wapInfo));
                    normal = true;
                    break;
                }
            }
            if (!normal) {
                for (WapWindow wapWindow : wapInfo.getWapWindows()) {
                    if (wapWindow.getType().equals(WapWindowType.OpenFile)) {
                        fileOpenWapInfoListVo.getOthers().add(new WapBaseInfoVo(wapInfo));
                        break;
                    }
                }
            }
        }
        return ResponseBody.success(fileOpenWapInfoListVo);
    }

    /**
     * 获取系统注册的 文件类型
     */
    @GetMapping("/types")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseBody<List<FileTypeVo>> getFileTypes() {
        List<FileType> fileTypes = osFileTypeRepository.findAll();
        List<FileTypeVo> collect = fileTypes.stream().map(FileTypeVo::new).collect(Collectors.toList());
        return ResponseBody.success(collect);
    }


    /**
     * 获取文件选择器的 窗口 参数
     */
    @GetMapping("/select/window/option")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseBody<WapWindowOptionVo> selectFileWindow() throws IOException {
        OsSetting osSetting = osService.getOsSetting();
        String fileManagerSelectWapId = osSetting.getFileManagerSelectWapId();
        if (fileManagerSelectWapId != null) {
            WapInfo wapInfo = wapInfoRepository.findById(fileManagerSelectWapId).get();
            List<WapWindow> wapWindows = wapInfo.getWapWindows();
            for (WapWindow wapWindow : wapWindows) {
                if (wapWindow.getType().equals(WapWindowType.FileManagerSelect)) {
                    WapWindowOptionVo wapWindowOptionVo = new WapWindowOptionVo(wapWindow, wapInfo.getId());
                    return ResponseBody.success(wapWindowOptionVo);
                }
            }
        }
        return ResponseBody.fail(ResponseBodyCodeEnum.NOT_FOUND, "无法选择文件。没有安装对应的wap");
    }
}
