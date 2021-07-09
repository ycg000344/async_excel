package io.github.ycg000344.async.excel.handler;

import io.github.ycg000344.async.excel.bean.TaskProgress;

public interface TaskProcessCacheFunc {

    /**
     * 更新任务进度
     *
     * @param taskId
     * @param taskProgress
     */
    void updateTaskProcess(String taskId, TaskProgress taskProgress);

    /**
     * 查询任务进度
     *
     * @param taskId
     * @return
     */
    TaskProgress getTaskProcess(String taskId);


}
