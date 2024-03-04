package cn.donting.web.os.api.task;

import cn.donting.web.os.api.annotation.Nullable;
import cn.donting.web.os.api.wap.WapWindowOption;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 一个耗时的任务
 * <p>
 * 任务完成后 会自动移除
 * 用于在执行 一个耗时任务时，用户刷新了界面，丢失状态
 * 删除文件/复制文件等耗时任务时 能保留进度
 * 或者 用户直接关闭 浏览器。 等 任务执行完成后，能看到一个 任务 完成情况的反馈
 *
 * @author donting
 * @see TaskService
 */
@Getter
public abstract class Task {
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务Id
     */
    private final String taskId;
    /**
     * 任务描述
     */
    private String description;

    /**
     * 创建时间
     */
    private final long creatTime;
    /**
     * 停止时间
     */
    private long stopTime;
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 任务状态
     */
    private TaskState taskState;
    /**
     * error
     */
    private Exception exception;
    /**
     * 创建者(提交任务的用户)
     */
    private String username;
    /**
     * 隶属于哪个wap
     */
    private String wapId;
    /**
     * 用于显示这个 task 的 wapWindowOption
     * 为 null 则没有窗口可以显示其信息
     */
    @Nullable
    @Setter
    private WapWindowOption wapWindowOption;
    /**
     * 任务进度 0-100
     */
    private byte progress;

    public Task(String taskName, WapWindowOption wapWindowOption) {
        this.taskName = taskName;
        taskId = UUID.randomUUID().toString();
        taskState = TaskState.Creat;
        this.wapWindowOption = wapWindowOption;
        creatTime = System.currentTimeMillis();
        progress = 0;
    }

    public Task(String taskName) {
        this(taskName, null);
    }

    protected Task(Task task) {
        this.taskName = task.getTaskName();
        this.taskId = task.getTaskId();
        this.taskState = task.getTaskState();
        this.username = task.getUsername();
        this.wapId = task.getWapId();
        this.exception = null;
        this.endTime = task.getEndTime();
        this.startTime = task.getStartTime();
        this.stopTime = task.getStopTime();
        this.creatTime = task.getCreatTime();
    }

    /**
     * 在提交 task 时，有 {@link TaskService#submitTask(Task)} 自动设置
     * 不应该手动调用
     *
     * @param username 用户名， 提交的用户
     * @param wapId    task 隶属于哪个wap. {@link Object#getClass()}
     */
    public final void setInfo(String username, String wapId) {
        if (this.username != null) {
            return;
        }
        this.username = username;
        this.wapId = wapId;
    }

    /**
     * 开始任务
     */
    public synchronized final void start() {
        if (taskState != TaskState.Creat) {
            return;
        }
        try {
            startTime = System.currentTimeMillis();
            taskState = TaskState.Running;
            run();
            this.taskState = TaskState.End;
        } catch (Exception ex) {
            this.exception = ex;
            this.taskState = TaskState.ErrorEnd;
        }
        endTime = System.currentTimeMillis();

    }

    /**
     * 任务开始执行
     *
     * @throws Exception 异常
     */
    public abstract void run() throws Exception;

    public synchronized final void stop() {
        if (!taskState.equals(TaskState.Running)) {
            return;
        }
        stopTime = System.currentTimeMillis();
        taskState = TaskState.Stopping;
        this.cease();
    }

    /**
     * 正常停止任务
     */
    protected abstract void cease();

    /**
     * 任务正常完成后的消息 通知，
     * 应为任务完成后  wap可能被 jvm 卸载
     *
     * @return null 没有消息
     */
    public String taskFinishMsg() {
        return null;
    }

    public final boolean isEnd() {
        return taskState.equals(TaskState.End) || taskState.equals(TaskState.ErrorEnd);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return username + ": " + taskName;
    }
}
