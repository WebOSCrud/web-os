package cn.donting.web.os.core.vo;

import cn.donting.web.os.api.task.Task;
import cn.donting.web.os.api.task.TaskResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"exception","wapWindowOption"})
public class TaskVo extends TaskResult {
    private WapWindowOptionVo wapWindowOptionVo;
    public TaskVo(Task task) {
        super(task);
        if (task.getWapWindowOption()!=null) {
            wapWindowOptionVo=new WapWindowOptionVo(task.getWapWindowOption(),task.getWapId());
        }
    }
}
