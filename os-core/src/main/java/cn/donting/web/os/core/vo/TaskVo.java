package cn.donting.web.os.core.vo;

import cn.donting.web.os.api.task.Task;
import cn.donting.web.os.api.task.TaskResult;
import cn.donting.web.os.api.wap.WapWindowOption;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"exception","wapWindowOption"})
public class TaskVo extends TaskResult {
    private WapWindowOption wapWindowOption;
    public TaskVo(Task task) {
        super(task);
        if (task.getWapWindowOption()!=null) {
            wapWindowOption=task.getWapWindowOption();
        }
    }
}
