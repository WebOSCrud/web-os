package cn.donting.web.os.api.task;

import lombok.Getter;
import lombok.ToString;

/**
 * Task 结果。task 完成后保留task 结果
 * 应为wap 可以会被回收,因为 task 是 wap 自己实现的。为了能正常卸载 wap Classloader, 就转移 task的结果
 */
@Getter
@ToString
public  class TaskResult extends Task{

    private String exceptionMsg;

    public TaskResult(Task task) {
        super(task);
        Exception exception = task.getException();
        //保留 exception 堆栈异常
        if(exception!=null){
            StringBuilder result = new StringBuilder();
            result.append(exception.toString()).append("\n");
            for (StackTraceElement element : exception.getStackTrace()) {
                result.append("\tat ").append(element).append("\n");
            }
            exceptionMsg=result.toString();
        }
    }

    @Override
    public void run() throws Exception {

    }

    @Override
    protected void cease() {

    }
}
