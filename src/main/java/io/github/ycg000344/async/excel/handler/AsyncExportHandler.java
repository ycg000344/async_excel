package io.github.ycg000344.async.excel.handler;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import io.github.ycg000344.async.excel.constant.ExportTitleEnum;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AsyncExportHandler {

    int PAGE_SIZE = 100;

    List doSelect(Map param);

    ExportTitleEnum titleType();

    Class pojoClass();

    Map<String, String> titleMap();

    Map param();

    default List selectForExport(Map param, int page) {
        PageInfo pageInfo = PageHelper.startPage(page, PAGE_SIZE).doSelectPageInfo(
                () -> doSelect(param)
        );
        List list = pageInfo.getPages() >= page ? pageInfo.getList() : new ArrayList<>();
        return conversion(list);
    }

    default List conversion(List list) {
        return list;
    }

    default int pages(Map param) {
        PageInfo pageInfo = PageHelper.startPage(1, PAGE_SIZE).doSelectPageInfo(
                () -> doSelect(param)
        );
        return pageInfo.getPages();
    }

    default List<ExcelExportEntity> title() {
        List<ExcelExportEntity> list = new ArrayList<>();
        Map<String, String> map = this.titleMap();
        if (CollectionUtil.isEmpty(map)) return list;
        map.forEach((k, v) -> list.add(new ExcelExportEntity(v, k)));
        return list;
    }

    default ExportParams entity() {
        ExportParams params = new ExportParams();
        params.setType(ExcelType.XSSF);
        return params;
    }

}
