package cn.donting.web.os.core.controller;

import cn.donting.web.os.api.wap.WapInstallInfo;
import cn.donting.web.os.core.db.repository.IWapInstallInfoRepository;
import cn.donting.web.os.core.service.WapRuntimeService;
import cn.donting.web.os.core.service.WapService;
import cn.donting.web.os.core.vo.ResponseBody;
import cn.donting.web.os.core.vo.WapVo;
import cn.donting.web.os.core.vo.param.WapIdPar;
import cn.donting.web.os.core.wap.Wap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    final IWapInstallInfoRepository wapInstallInfoRepository;
    final WapService wapService;

    public WapController(WapRuntimeService wapRuntimeService, IWapInstallInfoRepository wapInstallInfoRepository, WapService wapService) {
        this.wapRuntimeService = wapRuntimeService;
        this.wapInstallInfoRepository = wapInstallInfoRepository;
        this.wapService = wapService;
    }

    /**
     * 获取安装列表
     *
     * @return
     */
    @GetMapping("/installs")
    public ResponseBody<List<WapInstallInfo>> getInstallList() {
        List<WapInstallInfo> installList = wapService.getInstallList();
        return ResponseBody.success(installList);
    }

    /**
     * 卸载wap
     *
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
     *
     * @return
     */
    @GetMapping("/runtimes")
    public ResponseBody<List<WapVo>> getWapRuntimeList() {
        List<Wap> runtimes = wapRuntimeService.getAllRuntime();
        List<WapVo> wapVos=new ArrayList<>();
        for (Wap runtime : runtimes) {
            WapInstallInfo wapInstallInfo = wapInstallInfoRepository.findById(runtime.getWapInfo().getId()).get();
            WapVo wapVo = new WapVo(runtime, wapInstallInfo);
            wapVos.add(wapVo);
        }
        return ResponseBody.success(wapVos);
    }

    /**
     * 停止一个Wap
     *
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

}
