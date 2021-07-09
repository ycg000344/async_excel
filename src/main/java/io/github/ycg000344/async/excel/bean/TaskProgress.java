package io.github.ycg000344.async.excel.bean;

import io.github.ycg000344.async.excel.handler.TaskProcessCacheFunc;
import lombok.Builder;

@Builder
public class TaskProgress {

    private TaskInfo taskInfo;
    private Double percent;
    private Integer total;
    private Integer error;
    private Integer success;

    public void update(TaskProcessCacheFunc taskProcessCacheFunc) {
        taskProcessCacheFunc.updateTaskProcess(this.taskInfo.getTaskId(), this);
    }

}
