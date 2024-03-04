package cn.donting.web.os.core.controller;

import cn.donting.web.os.api.task.Task;
import cn.donting.web.os.api.task.TaskService;
import cn.donting.web.os.api.task.TaskResult;
import cn.donting.web.os.core.vo.ResponseBody;
import cn.donting.web.os.core.vo.ResponseBodyCodeEnum;
import cn.donting.web.os.core.vo.TaskVo;
import cn.donting.web.os.core.vo.param.TaskPar;
import cn.donting.web.os.core.vo.param.TaskWindowOptionPar;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task")
public class TaskController {


    final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

//    /**
//     * 获取所有用户的 task list
//     * @return
//     */
//    @GetMapping("/list")
//    public ResponseBody<List<TaskVo>> taskList() {
//        List<Task> tasks = taskService.taskList();
//        List<TaskVo> collect = tasks.stream().map(TaskVo::new).collect(Collectors.toList());
//        return ResponseBody.success(collect);
//    }

    /**
     * 获取当前登陆用户的 task list
     * @return
     */
    @GetMapping("/list/current/user")
    public ResponseBody<List<TaskVo>> taskListCurrentUser() {
        List<Task> tasks = taskService.taskListCurrentUser();
        List<TaskVo> collect = tasks.stream().map(TaskVo::new).collect(Collectors.toList());
        return ResponseBody.success(collect);
    }

    /**
     * 获取一个任务
     * @param taskId 任务id
     * @return
     */
    @GetMapping()
    public ResponseBody<TaskVo> task(@RequestParam String taskId) {
        Task task = taskService.getTask(taskId);
        return ResponseBody.success(new TaskVo(task));
    }

    /**
     * 确认一个已经完成的 任务。 确认后将 从系统移除，不在能获取
     *
     * @param taskPar
     * @return
     */
    @PostMapping("/confirm")
    public ResponseBody<TaskResult> confirm(@RequestBody TaskPar taskPar) {
        Task task = taskService.confirm(taskPar.getTaskId());
        return ResponseBody.success(new TaskVo(task));
    }

    /**
     * 停止 一个 任务
     * @param taskPar
     * @return
     */
    @PostMapping("/stop")
    public ResponseBody<TaskResult> stop(@RequestBody TaskPar taskPar) {
        String taskId = taskPar.getTaskId();
        Task task = taskService.getTask(taskId);
        if (task == null) {
            return ResponseBody.fail(ResponseBodyCodeEnum.NOT_FOUND);
        }
        task.stop();
        return ResponseBody.success(new TaskVo(task));
    }

    /**
     * 修改一个任务的 窗口参数
     * @param taskPar
     * @return
     */
    @PostMapping("/wap_window_option")
    public ResponseBody wapWindowOption(@RequestBody TaskWindowOptionPar taskPar) {
        String taskId = taskPar.getTaskId();
        Task task = taskService.getTask(taskId);
        if (task == null) {
            return ResponseBody.fail(ResponseBodyCodeEnum.NOT_FOUND);
        }
        task.setWapWindowOption(task.getWapWindowOption());
        return ResponseBody.success(new TaskVo(task));
    }


}
