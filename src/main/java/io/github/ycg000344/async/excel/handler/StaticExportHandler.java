package io.github.ycg000344.async.excel.handler;


import io.github.ycg000344.async.excel.constant.ExportTitleEnum;

import java.util.Map;

/**
 * @author lusheng
 * @since 2021-07-09
 */
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
