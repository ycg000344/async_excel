package io.github.ycg000344.async.excel.bean;

import io.github.ycg000344.async.excel.handler.TaskProcessCacheFunc;
import lombok.Builder;

/**
 * @author lusheng
 * @since 2021-07-09
 */
@Builder
public class TaskProgress {

    private TaskInfo taskInfo;
    private Double percent;
    private Integer total;
    private Integer error;
    private Integer success;

    /**
     * 更新进度
     *
     * @param taskProcessCacheFunc 执行更新
     */
    public void update(TaskProcessCacheFunc taskProcessCacheFunc) {
        taskProcessCacheFunc.updateTaskProcess(this.taskInfo.getTaskId(), this);
    }

}
