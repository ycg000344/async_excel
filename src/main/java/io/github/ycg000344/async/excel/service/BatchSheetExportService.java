package io.github.ycg000344.async.excel.service;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.export.ExcelExportService;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import cn.afterturn.easypoi.exception.excel.ExcelExportException;
import cn.afterturn.easypoi.exception.excel.enums.ExcelExportEnum;
import cn.afterturn.easypoi.util.PoiExcelGraphDataUtil;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import io.github.ycg000344.async.excel.bean.TaskInfo;
import io.github.ycg000344.async.excel.constant.ExportTitleEnum;
import io.github.ycg000344.async.excel.handler.AsyncExportHandler;
import io.github.ycg000344.async.excel.handler.TaskProcessCacheFunc;
import io.github.ycg000344.async.excel.util.AsyncExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lusheng
 * @since 2021-07-09
 */
@Slf4j
public class BatchSheetExportService extends ExcelExportService {


    protected String taskId;
    protected int totalSheets;

    protected TaskInfo taskInfo;
    protected TaskProcessCacheFunc taskProcessCacheFunc;

    protected SXSSFWorkbook workbook;
    protected List<ExcelExportEntity> excelParams;
    protected ExportParams entity;
    protected int titleHeight;
    protected Drawing patriarch;
    protected short rowHeight;
    protected int index;

    /**
     * @param taskId               ??????ID
     * @param totalSheets          ???sheet??????
     * @param taskInfo             ????????????
     * @param taskProcessCacheFunc ??????????????????
     */
    public BatchSheetExportService(String taskId, int totalSheets, TaskInfo taskInfo, TaskProcessCacheFunc taskProcessCacheFunc) {
        this.taskId = taskId;
        this.totalSheets = totalSheets;
        this.taskInfo = taskInfo;
        this.taskProcessCacheFunc = taskProcessCacheFunc;
        initial();
    }


    private void initial() {
        this.workbook = new SXSSFWorkbook();
    }

    private Sheet create(ExportParams entity, Class pojoClass) {
        List<ExcelExportEntity> entities = createExcelExportEntityList(entity, pojoClass);
        return create(entity, entities);
    }

    private Sheet create(ExportParams entity, List<ExcelExportEntity> excelParams) {
        entity.setType(ExcelType.XSSF);
        this.entity = entity;
        this.excelParams = excelParams;
        super.type = entity.getType();
        Sheet sheet = createSheet();
        if (entity.getMaxNum() == 0) entity.setMaxNum(ExcelExportUtil.USE_SXSSF_LIMIT);
        insertDataToSheet(workbook, entity, excelParams, null, sheet);
        return sheet;
    }

    protected void insertDataToSheet(Workbook workbook, ExportParams entity,
                                     List<ExcelExportEntity> entityList, Collection<?> dataSet,
                                     Sheet sheet) {
        try {
            dataHandler = entity.getDataHandler();
            if (dataHandler != null && dataHandler.getNeedHandlerFields() != null) {
                needHandlerList = Arrays.asList(dataHandler.getNeedHandlerFields());
            }
            dictHandler = entity.getDictHandler();
            // ??????????????????
            setExcelExportStyler((IExcelExportStyler) entity.getStyle()
                    .getConstructor(Workbook.class).newInstance(workbook));
            patriarch = PoiExcelGraphDataUtil.getDrawingPatriarch(sheet);
            List<ExcelExportEntity> excelParams = new ArrayList<ExcelExportEntity>();
            if (entity.isAddIndex()) {
                excelParams.add(indexExcelEntity(entity));
            }
            excelParams.addAll(entityList);
            sortAllParams(excelParams);
            this.index = entity.isCreateHeadRows()
                    ? createHeaderAndTitle(entity, sheet, workbook, excelParams) : 0;
            titleHeight = index;
            setCellWith(excelParams, sheet);
            setColumnHidden(excelParams, sheet);
            rowHeight = getRowHeight(excelParams);
            setCurrentIndex(1);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
        }
    }

    private Sheet createSheet() {
        Sheet sheet;
        if (workbook != null && entity != null && excelParams != null) {
            try {
                try {
                    if (StrUtil.isNotEmpty(entity.getSheetName())) {
                        sheet = workbook.createSheet(entity.getSheetName());
                    } else {
                        sheet = workbook.createSheet();
                    }
                } catch (Exception var5) {
                    sheet = workbook.createSheet();
                }
            } catch (Exception var6) {
                throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, var6);
            }
        } else {
            throw new ExcelExportException(ExcelExportEnum.PARAMETER_ERROR);
        }
        return sheet;
    }

    /**
     * @param sheetIndex ??????sheet??????
     * @param handler    ??????
     * @param sheet      ??????sheet
     */
    public void export(int sheetIndex, AsyncExportHandler handler, Sheet sheet) {
        int page = 1;
        Map param = handler.param();
        int pages = handler.pages(param);
        List list = handler.selectForExport(param, page++);
        while (CollectionUtil.isNotEmpty(list)) {
            sheet = write(sheet, list);
            updateProgress(page - 1, pages, sheetIndex);
            list = handler.selectForExport(param, page++);
        }
        close(sheet);
    }

    private void updateProgress(int page, int pages, int sheetIndex) {
        double row = (double) page / (double) pages;
        double sheet = (double) sheetIndex / (double) this.totalSheets;
        double v = (row * sheet) / (100 * 100);
        AsyncExcelUtils.updateTaskProcess(taskProcessCacheFunc, taskInfo, v, 0, 0);
    }

    private void close(Sheet sheet) {
        if (entity.getFreezeCol() != 0) {
            sheet.createFreezePane(this.entity.getFreezeCol(), this.titleHeight, this.entity.getFreezeCol(), this.titleHeight);
        }
        mergeCells(sheet, this.excelParams, this.titleHeight);
        // this.addStatisticsRow(this.getExcelExportStyler().getStyles(true, (ExcelExportEntity) null), sheet);
        addStatisticsRow(getExcelExportStyler().getStyles(true, null), sheet);
    }

    private Sheet write(Sheet sheet, List data) {
        if (sheet.getLastRowNum() + data.size() > this.entity.getMaxNum()) {
            sheet = this.workbook.createSheet();
            index = 0;
        }
        for (Object t : data) {
            try {
                index += createCells(patriarch, index, t, excelParams, sheet, workbook, rowHeight, 0)[0];
            } catch (Exception e) {
                log.error((e.getMessage()), e);
                throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
            }
        }
        return sheet;
    }

    /**
     * @return Workbook
     */
    public SXSSFWorkbook get() {
        return this.workbook;
    }


    /**
     * @param handler ??????
     * @return sheet
     */
    public Sheet create(AsyncExportHandler handler) {
        if (handler.titleType().equals(ExportTitleEnum.STATIC)) {
            return create(handler.entity(), handler.pojoClass());
        }
        return create(handler.entity(), handler.title());
    }

    /**
     * @param entity    ????????????
     * @param pojoClass ????????????
     * @return ????????????
     */
    public List<ExcelExportEntity> createExcelExportEntityList(ExportParams entity, Class<?> pojoClass) {
        try {
            List<ExcelExportEntity> excelParams = new ArrayList();
            if (entity.isAddIndex()) {
                excelParams.add(this.indexExcelEntity(entity));
            }

            Field[] fileds = PoiPublicUtil.getClassFields(pojoClass);
            ExcelTarget etarget = (ExcelTarget) pojoClass.getAnnotation(ExcelTarget.class);
            String targetId = etarget == null ? null : etarget.value();
            this.getAllExcelField(entity.getExclusions(), targetId, fileds, excelParams, pojoClass, (List) null, (ExcelEntity) null);
            this.sortAllParams(excelParams);
            return excelParams;
        } catch (Exception var7) {
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, var7);
        }
    }
}
