package cn.async.excel.util;

import cn.hutool.core.io.file.FileNameUtil;
import cn.async.excel.bean.TaskInfo;
import cn.async.excel.bean.TaskProgress;
import cn.async.excel.constant.AsyncExcelConstant;
import org.springframework.data.redis.core.RedisTemplate;

public class AsyncExcelUtils {

    public static boolean isXlsx(String filename) {
        return AsyncExcelConstant.XLSX.equalsIgnoreCase(FileNameUtil.extName(filename));
    }

    public static void updateTaskProcess(RedisTemplate redisTemplate, TaskInfo taskInfo, Double percent, Integer total, Integer error) {
        TaskProgress.builder()
                .taskInfo(taskInfo)
                .percent(percent)
                .total(total)
                .error(error)
                .success(total - error)
                .build()
                .update(redisTemplate);
    }

    public static String taskProgressRedisKey(String taskId) {
        return String.format("async:excel:%s", taskId);
    }

}
