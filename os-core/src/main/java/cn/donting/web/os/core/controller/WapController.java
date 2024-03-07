package cn.donting.web.os.core.controller;

import cn.donting.web.os.api.wap.WapInstallInfo;
import cn.donting.web.os.core.db.repository.IWapInstallInfoRepository;
import cn.donting.web.os.core.file.OSFileSpaces;
import cn.donting.web.os.core.loader.WapLoader;
import cn.donting.web.os.core.service.WapRuntimeService;
import cn.donting.web.os.core.service.WapService;
import cn.donting.web.os.core.util.FileUtil;
import cn.donting.web.os.core.vo.ResponseBody;
import cn.donting.web.os.core.vo.WapInfoVo;
import cn.donting.web.os.core.vo.WapVo;
import cn.donting.web.os.core.vo.param.WapIdPar;
import cn.donting.web.os.core.wap.Wap;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * wap  相关的 控制器.
 * 运行时
 * 卸载
 *
 * @see WapRuntimeService
 * @see IWapInstallInfoRepository
 * @see cn.donting.web.os.core.service.WapService
 */
@RestController
@RequestMapping("/wap")
public class WapController {

    final WapRuntimeService wapRuntimeService;
    final WapService wapService;

    public WapController(WapRuntimeService wapRuntimeService, WapService wapService) {
        this.wapRuntimeService = wapRuntimeService;
        this.wapService = wapService;
    }

    /**
     * 获取安装列表
     * @return
     */
    @GetMapping("/installs")
    public ResponseBody<List<WapInfoVo>> getInstallList() {
        List<WapInstallInfo> installList = wapService.getInstallList();
        //TODO
//        List<WapInfoVo> collect = installList.stream().map(WapInfoVo::new).collect(Collectors.toList());
        return ResponseBody.success();
    }

    /**
     * 卸载wap
     * @param wapId
     * @return
     * @throws Exception
     */
    @PostMapping("/uninstall")
    public ResponseBody uninstall(@RequestBody WapIdPar wapId) throws Exception {
        wapService.uninstall(wapId.getWapId());
        return ResponseBody.success();
    }

    /**
     * 获取wap 运行时 状态
     * @return
     */
    @GetMapping("/runtimes")
    public ResponseBody<List<WapVo>> getWapRuntimeList() {
        List<Wap> runtimes = wapRuntimeService.getAllRuntime();
        List<WapVo> collect = runtimes.stream().map(wap -> new WapVo(wap)).collect(Collectors.toList());
        return ResponseBody.success(collect);
    }

    /**
     * 停止一个Wap
     * @param wapId
     * @return
     * @throws Exception
     */
    @PostMapping("/stop")
    public ResponseBody stop(@RequestBody WapIdPar wapId) throws Exception {
        String id = wapId.getWapId();
        wapRuntimeService.stop(id);
        return ResponseBody.success();
    }
    /**
     * 从wap 中获取一个 Resource，一般指 图片资源
     * @param wapId
     * @return
     * @throws Exception
     */
    @GetMapping("/resource/image")
    public ResponseEntity resource(String wapId, String resource) throws Exception {

        File wapInstallFile = OSFileSpaces.getWapInstallFile(wapId);
        Wap load = WapLoader.getWapLoader(wapInstallFile).load();
        URL resourceURL = load.getWapClassLoader().getResource(resource);
        if(resourceURL==null){
            return ResponseEntity.notFound().build();
        }
        URLConnection connection = resourceURL.openConnection();
        InputStream inputStream = connection.getInputStream();
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        MediaType imageMediaType = FileUtil.getImageMediaType(resource);
        return ResponseEntity.ok().contentType(imageMediaType).body(inputStreamResource);
    }

}
