package cn.donting.web.os.core.vo;

import cn.donting.web.os.api.wap.WapInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

public class FileOpenWapInfoListVo {
    private List<WapBaseInfoVo> normal=new ArrayList<>();
    private List<WapBaseInfoVo> others=new ArrayList<>();
}
