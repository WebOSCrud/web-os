package cn.donting.web.os.core.controller;

import cn.donting.web.os.api.wap.WapWindow;
import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.db.entity.OsFileType;
import cn.donting.web.os.core.db.entity.WapResource;
import cn.donting.web.os.core.db.repository.IOsFileTypeRepository;
import cn.donting.web.os.core.db.repository.IWapInstallInfoRepository;
import cn.donting.web.os.core.db.repository.IWapResourceRepository;
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
    final IWapInstallInfoRepository wapInfoRepository;
    final IWapResourceRepository wapResourceRepository;
    final OsService osService;

    private Set<String> imgExtName;

    public FIleController(IOsFileTypeRepository osFileTypeRepository, IWapInstallInfoRepository wapInfoRepository, IWapResourceRepository wapResourceRepository, OsService osService) {
        this.osFileTypeRepository = osFileTypeRepository;
        this.wapInfoRepository = wapInfoRepository;
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
    public ResponseEntity fileIcon(String path, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        //文件扩展名
        String extName = FileUtil.extName(file).toLowerCase();
        if (imgExtName.contains(extName)) {
            MediaType imageMediaType = FileUtil.getImageMediaType("name." + extName);
            return ResponseEntity.ok().contentType(imageMediaType).body(new FileInputStream(file));
        }
        if (extName.equals(OsCoreApplication.WPA_EXT_NAME) || extName.equals(OsCoreApplication.WPA_DEV_EXT_NAME)) {
            httpServletRequest.getRequestDispatcher("/" + OsCoreApplication.OS_ID + "/img/wap-file.png").forward(httpServletRequest, httpServletResponse);
            return null;
        } else {
            Optional<OsFileType> fileTypeOp = osFileTypeRepository.findById(extName);
            if (!fileTypeOp.isPresent()) {
                httpServletRequest.getRequestDispatcher("/" + OsCoreApplication.OS_ID + "/img/unknown-file.png").forward(httpServletRequest, httpServletResponse);
                return null;
            }
            OsFileType fileType = fileTypeOp.get();
            String wapId = fileType.getWapId();
            String wapResourceId = ResourceUtil.getWapResourceId(wapId, fileType.getIconResource());
            Optional<WapResource> wapResourceOp = wapResourceRepository.findById(wapResourceId);
            if (wapResourceOp.isPresent()) {
                httpServletRequest.getRequestDispatcher("/" + OsCoreApplication.OS_ID + "/img/unknown-file.png").forward(httpServletRequest, httpServletResponse);
                return null;
            }
            WapResource wapResource = wapResourceOp.get();
            String fileName = wapResource.getFileName();

            File wapResourcesDir = new File(OSFileSpaces.OS_WAP_RESOURCES, wapId);
            File imgFile = new File(wapResourcesDir, wapResource.getFileName());
            InputStream inputStream = new FileInputStream(imgFile);
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            MediaType imageMediaType = FileUtil.getImageMediaType(fileName);
            return ResponseEntity.ok().contentType(imageMediaType).body(inputStreamResource);
        }
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
        return ResponseBody.success(fileOpenWapInfoListVo);
    }

    /**
     * 获取系统注册的 文件类型
     */
    @GetMapping("/types")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseBody<List<FileTypeVo>> getFileTypes() {
        List<OsFileType> fileTypes = osFileTypeRepository.findAll();
        List<FileTypeVo> collect = fileTypes.stream().map(FileTypeVo::new).collect(Collectors.toList());
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
