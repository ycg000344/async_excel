package io.github.ycg000344.async.excel.handler;


import io.github.ycg000344.async.excel.constant.ExportTitleEnum;

public abstract class DynamicExportHandler implements AsyncExportHandler {

    @Override
    public ExportTitleEnum titleType() {
        return ExportTitleEnum.DYNAMIC;
    }

    @Override
    public Class pojoClass() {
        throw new UnsupportedOperationException();
    }
}
