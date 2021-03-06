package io.github.ycg000344.async.excel.runner;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.monitorjbl.xlsx.StreamingReader;
import io.github.ycg000344.async.excel.bean.ImportHandlerResult;
import io.github.ycg000344.async.excel.bean.TaskInfo;
import io.github.ycg000344.async.excel.handler.AsyncImportHandler;
import io.github.ycg000344.async.excel.handler.TaskProcessCacheFunc;
import io.github.ycg000344.async.excel.util.AsyncExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lusheng
 * @since 2021-07-09
 */
@Slf4j
public class AsyncExcelImportRunner implements Runnable {

    protected TaskInfo taskInfo;
    protected SqlSessionFactory sqlSessionFactory;
    protected AsyncImportHandler handler;
    protected TaskProcessCacheFunc taskProcessCacheFunc;

    protected String taskId;
    protected String sourceExcelFile;
    protected String errorExcelFile;

    protected List<String> titles;
    protected List<Row> errorRows;
    protected Map<Integer, String> errorMsgMap;
    protected int sheetTotalRowNum;

    private final int rowCacheSize = 100;
    private final int bufferSize = 1024 << 1;

    private AsyncExcelImportRunner() {
    }

    /**
     * @param taskInfo             任务信息
     * @param handler              处理
     * @param sqlSessionFactory    sqlsession
     * @param taskProcessCacheFunc 更新任务进度
     */
    public AsyncExcelImportRunner(TaskInfo taskInfo,
                                  AsyncImportHandler handler,
                                  SqlSessionFactory sqlSessionFactory,
                                  TaskProcessCacheFunc taskProcessCacheFunc) {
        this.taskInfo = taskInfo;
        this.handler = handler;
        this.sqlSessionFactory = sqlSessionFactory;
        this.taskProcessCacheFunc = taskProcessCacheFunc;

        initial();
    }

    private void initial() {

        this.taskId = taskInfo.getTaskId();
        this.sourceExcelFile = taskInfo.getSourceFilePath();
        this.errorExcelFile = taskInfo.getDownloadFilePath();

        this.titles = new ArrayList<>();
        this.errorRows = new ArrayList<>();
        this.errorMsgMap = new LinkedHashMap<>();
    }

    @Override
    public void run() {
        log.debug("[Async Excel] taskId: {}, [parse] , start.", this.taskId);
        AsyncExcelUtils.updateTaskProcess(taskProcessCacheFunc, taskInfo, 0d, 0, 0);
        SqlSession sqlSession = null;
        try (
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(rowCacheSize)
                        .bufferSize(bufferSize)
                        .open(new File(sourceExcelFile));
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            this.sheetTotalRowNum = sheet.getLastRowNum();
            sqlSession = sqlSessionFactory.openSession(false);
            handler.getMapper(sqlSession);
            sheet.rowIterator().forEachRemaining(this::accept);
            sqlSession.commit();
            log.debug("[Async Excel] taskId: {}, [parse] , success.", this.taskId);
        } catch (Exception e) {
            log.error("[Async Excel] taskId: {}, [parse] , exception:{}", this.taskId, e);
            if (Objects.nonNull(sqlSession)) sqlSession.rollback();
        }
        sqlSession = null;
        if (CollectionUtil.isEmpty(errorRows) || !handler.needFailure()) {
            AsyncExcelUtils.updateTaskProcess(taskProcessCacheFunc, taskInfo, 100d, this.sheetTotalRowNum, 0);
        } else {
            log.debug("[Async Excel] taskId: {}, [failure] , start.", this.taskId);
            this.createFailureFile();
            log.debug("[Async Excel] taskId: {}, [failure] , success.", this.taskId);
        }
    }

    private void createFailureFile() {
        File file = new File(this.errorExcelFile);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        SXSSFWorkbook workbook = null;
        OutputStream outputStream = null;
        try {
            workbook = createBook();
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.dispose();

            outputStream.flush();
        } catch (Exception e) {
            log.error("[Async Excel] taskId: {}, [createFailureFile] , exception: {}", this.taskId, e);
        } finally {
            try {
                if (Objects.nonNull(workbook)) {
                    workbook.dispose();
                    workbook.close();
                    workbook = null;
                }
                if (Objects.nonNull(outputStream)) {
                    outputStream.close();
                    outputStream = null;
                }
            } catch (Exception e) {
                log.error("[Async Excel] taskId: {}, [createFailureFile] , exception: {}", this.taskId, e);
            }
        }
        AsyncExcelUtils.updateTaskProcess(taskProcessCacheFunc, taskInfo, 100d, this.sheetTotalRowNum, this.errorRows.size());
    }

    private SXSSFWorkbook createBook() {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        int r = 0;
        Row row = sheet.createRow(r++);
        titleFill(row);
        for (Row errorRow : this.errorRows) {
            row = sheet.createRow(r++);
            int c = 0;
            Cell cell;
            Cell errorCell;
            for (int i = 0; i < this.titles.size(); i++) {
                cell = row.createCell(c++);
                errorCell = errorRow.getCell(i);
                if (Objects.nonNull(errorCell))
                    cell.setCellValue(StrUtil.emptyToDefault(errorCell.getStringCellValue(), StrUtil.EMPTY));
            }
            cell = row.createCell(c++);
            cell.setCellValue(errorMsgMap.get(errorRow.getRowNum()));
        }
        return workbook;
    }

    private void titleFill(Row row) {
        int c = 0;
        Cell cell;
        for (String title : this.titles) {
            cell = row.createCell(c++);
            cell.setCellValue(title);
        }
        cell = row.createCell(c++);
        String last = "异常原因";
        cell.setCellValue(last);
    }

    private void accept(Row row) {
        if (Objects.isNull(row)) return;
        if (row.getRowNum() == 0) {
            row.forEach(c -> titles.add(StrUtil.emptyToDefault(c.getStringCellValue(), StrUtil.EMPTY)));
            handler.header(row);
            return;
        }
        ImportHandlerResult handlerResult = handler.handle(row);
        updateProgress(row.getRowNum());
        if (handlerResult.isOk()) return;
        errorRows.add(row);
        errorMsgMap.put(row.getRowNum(), handlerResult.getMsg());
    }

    private void updateProgress(int rowNum) {
        if ((1 & rowNum) == 1) return;
        AsyncExcelUtils.updateTaskProcess(this.taskProcessCacheFunc, taskInfo,
                Math.min((rowNum * 1.0d / this.sheetTotalRowNum * 100), 90d),
                this.sheetTotalRowNum, 0);
    }

}
