package io.github.ycg000344.async.excel.handler;

import io.github.ycg000344.async.excel.bean.TaskProgress;

/**
 * @author lusheng
 * @since 2021-07-09
 */
public interface TaskProcessCacheFunc {

    /**
     * 更新任务进度
     *
     * @param taskId       任务ID
     * @param taskProgress 任务进度
     */
    void updateTaskProcess(String taskId, TaskProgress taskProgress);

    /**
     * 查询任务进度
     *
     * @param taskId 任务ID
     * @return 任务进度
     */
    TaskProgress getTaskProcess(String taskId);


}
