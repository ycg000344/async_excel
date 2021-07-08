package cn.async.excel.handler;


import cn.async.excel.constant.ExportTitleEnum;

import java.util.Map;

public abstract class StaticExportHandler implements AsyncExportHandler {

    @Override
    public ExportTitleEnum titleType() {
        return ExportTitleEnum.STATIC;
    }

    @Override
    public Map<String, String> titleMap() {
        throw new UnsupportedOperationException();
    }
}
