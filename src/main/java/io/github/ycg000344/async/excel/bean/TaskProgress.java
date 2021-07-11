package io.github.ycg000344.async.excel.bean;

import io.github.ycg000344.async.excel.handler.TaskProcessCacheFunc;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lusheng
 * @since 2021-07-09
 */
@Builder
@Slf4j
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
        try {
            log.info("[Async Excel] taskId: {}, percent: {}", this.taskInfo.getTaskId(), this.percent);
            taskProcessCacheFunc.updateTaskProcess(this.taskInfo.getTaskId(), this);
        } catch (Exception e) {
            log.error("[Async Excel] taskId: {},Exception: {}", this.taskInfo.getTaskId(), e);
        }
    }

}
