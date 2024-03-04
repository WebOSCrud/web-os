package cn.donting.web.os.core.service;

import cn.donting.web.os.api.exception.TaskException;
import cn.donting.web.os.api.task.Task;
import cn.donting.web.os.api.task.TaskState;
import cn.donting.web.os.core.db.entity.User;
import cn.donting.web.os.core.exception.ResponseException;
import cn.donting.web.os.core.loader.WapClassLoader;
import cn.donting.web.os.api.task.TaskResult;
import cn.donting.web.os.core.vo.ResponseBody;
import cn.donting.web.os.core.vo.ResponseBodyCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * task 服务类
 */
@Slf4j
@Service
public class TaskService implements cn.donting.web.os.api.task.TaskService {

    private Map<String, Task> taskMap = new ConcurrentHashMap<>();
    private Map<String, TaskResult> taskResultMap = new ConcurrentHashMap<>();
    private final UserService userService;

    public TaskService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public <T extends Task> T submitTask(T task) {
        if (task.getTaskState() != TaskState.Creat) {
            throw new TaskException("task State 不是 TaskState.Creat");
        }
        User loginUser = userService.getLoginUser();
        if(loginUser==null){
            throw new TaskException("不是登陆用户操作的");
        }
        ClassLoader classLoader = task.getClass().getClassLoader();
        if(classLoader instanceof WapClassLoader){
            String wapId = ((WapClassLoader) classLoader).getWapId();
            task.setInfo(loginUser.getName(),wapId);
        }else {
            throw new TaskException("不是重Wap加载的Task");
        }
        taskMap.put(task.getTaskId(), task);
        return task;
    }

    @Override
    public List<Task> taskList() {
        Collection<Task> values = taskMap.values();
        ArrayList<Task> tasks = new ArrayList<>(values);
        tasks.addAll(taskResultMap.values());
        return tasks;
    }

    @Override
    public List<Task> taskListCurrentUser() {
        User loginUser = userService.getLoginUser();
        List<Task> tasks = taskList();
        tasks.removeIf(task -> !task.getUsername().equals(loginUser.getName()));
        return tasks;
    }

    @Override
    public <T extends Task> List<T> taskList(Class<T> taskClass) {
        List<Task> tasks = taskList();
        List<T> rest = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getClass().isAssignableFrom(taskClass)) {
                rest.add((T) task);
            }
        }
        return rest;
    }

    @Override
    public Task getTask(String taskId) {
        Task task = taskMap.get(taskId);
        if(task==null){
            TaskResult taskResult = taskResultMap.get(taskId);
            return taskResult;
        }
        return taskMap.get(taskId);
    }

    @Override
    public Task confirm(String taskId) {
        Task task = taskMap.get(taskId);
        if (task != null) {
            if (!task.isEnd()) {
                throw new TaskException("不能确认一个未完成的Task");
            }
            taskMap.remove(taskId);
            return task;
        }
        TaskResult taskResult = this.taskResultMap.remove(taskId);
        return taskResult;
    }

    /**
     * 1分钟检查一次 任务状态.把 结束的 移出来
     */
    @Scheduled(fixedDelay = 1 * 60 * 1000)
    private void taskCheck() {
        List<Task> tasks = taskList();
        for (Task task : tasks) {
            if (task.getTaskState().equals(TaskState.End) || task.getTaskState().equals(TaskState.ErrorEnd)) {
                taskResultMap.remove(task.getTaskId());
                log.info("task:{} 结束，转移",task.getTaskId());
                TaskResult taskResult = new TaskResult(task);
                this.taskResultMap.put(taskResult.getTaskId(), taskResult);
            }
        }
    }
}
