package io.github.ycg000344.async.excel.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.github.ycg000344.async.excel.bean.TaskInfo;
import io.github.ycg000344.async.excel.constant.AsyncExcelConstant;
import io.github.ycg000344.async.excel.constant.ParseEnum;
import io.github.ycg000344.async.excel.handler.AsyncExportHandler;
import io.github.ycg000344.async.excel.handler.AsyncImportHandler;
import io.github.ycg000344.async.excel.handler.FileTransferFunc;
import io.github.ycg000344.async.excel.handler.TaskProcessCacheFunc;
import io.github.ycg000344.async.excel.properties.AsyncExcelProperties;
import io.github.ycg000344.async.excel.runner.AsyncExcelExportRunner;
import io.github.ycg000344.async.excel.runner.AsyncExcelImportRunner;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class AsyncExcelTaskManager {

    private static String baseDir = System.getProperty("user.dir") + File.separator;

    public TaskInfo createExportTask(AsyncExportHandler handler, Executor service, TaskProcessCacheFunc taskProcessCacheFunc) {
        TaskInfo build = newExportTask();
        AsyncExcelExportRunner runner = new AsyncExcelExportRunner(
                build,
                Arrays.asList(handler),
                taskProcessCacheFunc
        );
        service.execute(runner);
        return build;
    }

    public TaskInfo createExportTask(List<AsyncExportHandler> handlers, Executor service, TaskProcessCacheFunc taskProcessCacheFunc) {
        TaskInfo build = newExportTask();
        AsyncExcelExportRunner runner = new AsyncExcelExportRunner(
                build,
                handlers,
                taskProcessCacheFunc);
        service.execute(runner);
        return build;
    }

    public TaskInfo createImportTask(FileTransferFunc func, AsyncImportHandler handler, ExecutorService service, SqlSessionFactory sqlSessionFactory, TaskProcessCacheFunc taskProcessCacheFunc) throws Exception {
        TaskInfo build = newImportTask();
        func.transferTo(build.getSourceFilePath());
        AsyncExcelImportRunner runner = new AsyncExcelImportRunner(
                build,
                handler,
                sqlSessionFactory,
                taskProcessCacheFunc
        );
        service.submit(runner);
        return build;
    }

    private String parentDIr(ParseEnum anEnum) {
        return baseDir + anEnum.getValue() + File.separator + DateUtil.today() + File.separator;
    }

    private String fileName(String taskId) {
        return taskId + StrUtil.DOT + AsyncExcelConstant.XLSX;
    }

    private String taskId() {
        return IdUtil.getSnowflake(1, 1).nextIdStr();
    }

    private TaskInfo newImportTask() {
        TaskInfo info = newExportTask();
        info.setParseEnum(ParseEnum.IMPORT);
        info.setSourceFilePath(parentDIr(ParseEnum.IMPORT) + info.getFileName());
        return info;
    }

    private TaskInfo newExportTask() {
        String taskId = taskId();
        String fileName = fileName(taskId);
        String downloadFile = parentDIr(ParseEnum.EXPORT) + fileName;
        return TaskInfo.builder()
                .taskId(taskId)
                .parseEnum(ParseEnum.EXPORT)
                .fileName(fileName)
                .downloadFilePath(downloadFile)
                .build();
    }

    protected void afterPropertiesSet(AsyncExcelProperties asyncExcelProperties) {
        if (StrUtil.isEmpty(asyncExcelProperties.getBaseDir())) return;
        File file = new File(asyncExcelProperties.getBaseDir());
        if (!file.exists()) file.mkdirs();
        baseDir = file.getAbsolutePath() + File.separator;
    }

}
