package io.github.ycg000344.async.excel.runner;

import io.github.ycg000344.async.excel.bean.TaskInfo;
import io.github.ycg000344.async.excel.handler.AsyncExportHandler;
import io.github.ycg000344.async.excel.handler.TaskProcessCacheFunc;
import io.github.ycg000344.async.excel.service.BatchSheetExportService;
import io.github.ycg000344.async.excel.util.AsyncExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

/**
 * @author lusheng
 * @since 2021-07-09
 */
@Slf4j
public class AsyncExcelExportRunner implements Runnable {

    protected TaskInfo taskInfo;
    protected List<AsyncExportHandler> handlers;
    protected TaskProcessCacheFunc taskProcessCacheFunc;

    protected String downloadFile;
    protected String taskId;

    private AsyncExcelExportRunner() {
    }

    /**
     * @param taskInfo             任务信息
     * @param handlers             处理
     * @param taskProcessCacheFunc 更新任务进度
     */
    public AsyncExcelExportRunner(TaskInfo taskInfo, List<AsyncExportHandler> handlers, TaskProcessCacheFunc taskProcessCacheFunc) {
        this.taskInfo = taskInfo;
        this.handlers = handlers;
        this.taskProcessCacheFunc = taskProcessCacheFunc;
        initial();
    }

    private void initial() {
        this.taskId = taskInfo.getTaskId();
        this.downloadFile = taskInfo.getDownloadFilePath();
    }

    @Override
    public void run() {
        log.info("[Async Excel] taskId: {}, start.", this.taskId);
        AsyncExcelUtils.updateTaskProcess(taskProcessCacheFunc, taskInfo, 0d, 0, 0);
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
            log.info("[Async Excel] taskId: {}, success.", this.taskId);
        } catch (Exception e) {
            log.error("[Async Excel] taskId: {}, Exception:{}", this.taskId, e);
        } finally {
            try {
                AsyncExcelUtils.updateTaskProcess(taskProcessCacheFunc, taskInfo, 100d, 0, 0);
                if (Objects.nonNull(workbook)) workbook.close();
                if (Objects.nonNull(outputStream)) outputStream.close();
            } catch (Exception e) {
                log.error("[Async Excel] taskId: {}, Exception: {}", this.taskId, e);
            }
        }
    }

    private Workbook export() {
        BatchSheetExportService service = new BatchSheetExportService(taskId, handlers.size(), taskInfo, taskProcessCacheFunc);
        for (int i = 0; i < handlers.size(); i++) {
            Sheet sheet = service.create(handlers.get(i));
            service.export(i, handlers.get(i), sheet);
        }
        return service.get();
    }

}

