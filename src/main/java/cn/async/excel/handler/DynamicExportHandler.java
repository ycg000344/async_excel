package cn.async.excel.handler;


import cn.async.excel.constant.ExportTitleEnum;

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
