package io.github.ycg000344.async.excel.handler;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.github.ycg000344.async.excel.constant.ExportTitleEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lusheng
 * @since 2021-07-09
 */
public interface AsyncExportHandler {

    int PAGE_SIZE = 100;
    Log LOG = Log.get();


    /**
     * @param param SQl 查询参数
     * @return 查询结果
     */
    List doSelect(Map param);

    /**
     * 表头类型
     *
     * @return 表头类型
     */
    ExportTitleEnum titleType();

    /**
     * 静态表头POJO
     *
     * @return 静态表头POJO
     * @see cn.afterturn.easypoi.excel.annotation.Excel
     */
    Class pojoClass();

    /**
     * 动态表头
     *
     * @return 动态表头
     */
    Map<String, String> titleMap();

    /**
     * 执行 doSelect方法的入参
     *
     * @return SQl 查询参数
     */
    Map param();

    /**
     * @param param SQl 查询参数
     * @param page  页码
     * @return 查询结果
     */
    default List selectForExport(Map param, int page) {
        Date start = new Date();
        PageInfo pageInfo = PageHelper.startPage(page, PAGE_SIZE).doSelectPageInfo(
                () -> doSelect(param)
        );
        LOG.debug("[Async Excel] , sql use time: {}", DateUtil.formatBetween(start, new Date()));
        List list = pageInfo.getPages() >= page ? pageInfo.getList() : new ArrayList<>();
        List conversion = conversion(list);
        LOG.debug("[Async Excel] , selectForExport use time: {}", DateUtil.formatBetween(start, new Date()));
        return conversion;
    }

    /**
     * @param list 结果
     * @return 转化结果
     */
    default List conversion(List list) {
        return list;
    }

    /**
     * @param param SQl 查询参数
     * @return 总页数
     */
    default int pages(Map param) {
        PageInfo pageInfo = PageHelper.startPage(1, PAGE_SIZE).doSelectPageInfo(
                () -> doSelect(param)
        );
        return pageInfo.getPages();
    }

    /**
     * @return 动态表头
     */
    default List<ExcelExportEntity> title() {
        List<ExcelExportEntity> list = new ArrayList<>();
        Map<String, String> map = this.titleMap();
        if (CollectionUtil.isEmpty(map)) return list;
        map.forEach((k, v) -> list.add(new ExcelExportEntity(v, k)));
        return list;
    }

    /**
     * @return 导出配置
     */
    default ExportParams entity() {
        ExportParams params = new ExportParams();
        params.setType(ExcelType.XSSF);
        return params;
    }

}
