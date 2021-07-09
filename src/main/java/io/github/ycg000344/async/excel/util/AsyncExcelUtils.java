package io.github.ycg000344.async.excel.util;

import cn.hutool.core.io.file.FileNameUtil;
import io.github.ycg000344.async.excel.bean.TaskInfo;
import io.github.ycg000344.async.excel.bean.TaskProgress;
import io.github.ycg000344.async.excel.constant.AsyncExcelConstant;
import io.github.ycg000344.async.excel.handler.TaskProcessCacheFunc;

/**
 * @author lusheng
 * @since 2021-07-09
 */
public class AsyncExcelUtils {

    /**
     * @param filename 文件名称
     * @return 匹配结果
     */
    public static boolean isXlsx(String filename) {
        return AsyncExcelConstant.XLSX.equalsIgnoreCase(FileNameUtil.extName(filename));
    }

    /**
     * @param taskProcessCacheFunc 更新任务进度
     * @param taskInfo             任务信息
     * @param percent              任务进度
     * @param total                总数
     * @param error                错误数
     */
    public static void updateTaskProcess(TaskProcessCacheFunc taskProcessCacheFunc, TaskInfo taskInfo, Double percent, Integer total, Integer error) {
        TaskProgress.builder()
                .taskInfo(taskInfo)
                .percent(percent)
                .total(total)
                .error(error)
                .success(total - error)
                .build()
                .update(taskProcessCacheFunc);
    }

}
