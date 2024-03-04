package cn.donting.web.os.api.task;

/**
 * 任务状态，不可逆
 */
public enum TaskState {
    /**
     * 创建时，提交任务是只能是该状态
     * @see TaskService#submitTask(Task)
     */
    Creat,
    /**
     * 任务运行中
     * @see Task#start()
     */
    Running,
    /**
     * 停止中
     */
    Stopping,
    /**
     * 任务正常结束
     */
    End,
    /**
     * 任务异常结束
     * 一般指 在{@link TaskState#Running} 中发生异常
     */
    ErrorEnd,
}
