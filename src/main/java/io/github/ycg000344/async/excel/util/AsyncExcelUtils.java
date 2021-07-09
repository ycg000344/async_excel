package io.github.ycg000344.async.excel.util;

import cn.hutool.core.io.file.FileNameUtil;
import io.github.ycg000344.async.excel.bean.TaskInfo;
import io.github.ycg000344.async.excel.bean.TaskProgress;
import io.github.ycg000344.async.excel.constant.AsyncExcelConstant;
import io.github.ycg000344.async.excel.handler.TaskProcessCacheFunc;

public class AsyncExcelUtils {

    public static boolean isXlsx(String filename) {
        return AsyncExcelConstant.XLSX.equalsIgnoreCase(FileNameUtil.extName(filename));
    }

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
