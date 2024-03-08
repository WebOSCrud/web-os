package cn.donting.web.os.core.domain.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FileOpenPar extends FilePathPar {
   private String wapId;
    private boolean def=false;
}
