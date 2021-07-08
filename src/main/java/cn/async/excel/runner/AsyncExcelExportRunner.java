package cn.async.excel.runner;

import cn.async.excel.util.AsyncExcelUtils;
import cn.async.excel.bean.TaskInfo;
import cn.async.excel.handler.AsyncExportHandler;
import cn.async.excel.service.BatchSheetExportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

@Slf4j
public class AsyncExcelExportRunner implements Runnable {

    protected TaskInfo taskInfo;
    protected List<AsyncExportHandler> handlers;
    protected RedisTemplate redisTemplate;

    protected String downloadFile;
    protected String taskId;

    private AsyncExcelExportRunner() {
    }

    public AsyncExcelExportRunner(TaskInfo taskInfo, List<AsyncExportHandler> handlers, RedisTemplate redisTemplate) {

        Assert.notNull(taskInfo);
        Assert.notNull(handlers);
        Assert.notNull(redisTemplate);

        this.taskInfo = taskInfo;
        this.handlers = handlers;
        this.redisTemplate = redisTemplate;
        initial();
    }

    private void initial() {
        this.taskId = taskInfo.getTaskId();
        this.downloadFile = taskInfo.getDownloadFilePath();
    }

    @Override
    public void run() {
        log.info("task:{},start.", this.taskId);
        AsyncExcelUtils.updateTaskProcess(redisTemplate, taskInfo, 0d, 0, 0);
        Workbook workbook = null;
        OutputStream outputStream = null;
        try {
            File file = new File(downloadFile);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            workbook = this.export();
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.flush();
            log.info("task:{},success.", this.taskId);
        } catch (Exception e) {
            log.error("task:{},error:{}", this.taskId, e);
        } finally {
            try {
                AsyncExcelUtils.updateTaskProcess(redisTemplate, taskInfo, 100d, 0, 0);
                if (Objects.nonNull(workbook)) workbook.close();
                if (Objects.nonNull(outputStream)) outputStream.close();
            } catch (Exception e) {
                log.error("task:{},error:{}", this.taskId, e);
            }
        }
    }

    private Workbook export() {
        BatchSheetExportService service = new BatchSheetExportService(taskId, handlers.size(), taskInfo, redisTemplate);
        for (int i = 0; i < handlers.size(); i++) {
            Sheet sheet = service.create(handlers.get(i));
            service.export(i, handlers.get(i), sheet);
        }
        return service.get();
    }

}

