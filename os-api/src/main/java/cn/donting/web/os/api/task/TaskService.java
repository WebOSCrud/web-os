package cn.donting.web.os.api.task;

import cn.donting.web.os.api.exception.TaskException;

import java.util.List;

/**
 * 任务 服务
 */
public interface TaskService {
    /**
     * 提交一个任务，只能提交 {@link TaskState#Creat} 状态的
     *
     * @param task
     * @param <T>
     * @return task
     * @throws TaskException
     */
    <T extends Task> T submitTask(T task);

    /**
     * 获取 任务列表,包括 {@link TaskResult}
     * @return 任务列表
     */
    List<Task> taskList();

    /**
     * 当前登陆用户创建的 task
     * 包括 {@link TaskResult}
     * @return task列表
     */
    List<Task> taskListCurrentUser();

    /**
     * 获取 某一个类型的 任务列表
     * 包括 {@link TaskResult}
     * @param taskClass 任务类型
     * @return 任务列表
     */
    <T extends Task> List<T> taskList(Class<T> taskClass);

    /**
     * 更具taskId 获取具体 的 Task
     * 包括 {@link TaskResult}
     * @param taskId taskId
     * @param <T>
     * @return Task
     */
    <T extends Task> T getTask(String taskId);

    /**
     * 确认一个 已完成的task,只能是 {@link TaskState#End} 或 {@link TaskState#ErrorEnd} 状态下
     * @param taskId 任务id
     * @throws TaskException
     */
    Task confirm(String taskId);

}
