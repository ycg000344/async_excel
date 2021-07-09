package io.github.ycg000344.async.excel.bean;

import lombok.Builder;
import lombok.Data;

/**
 * @author lusheng
 * @since 2021-07-09
 */
@Data
@Builder
public class ImportHandlerResult {

    private boolean ok;
    private String msg;

    /**
     * @param msg 错误信息
     * @return 处理结果
     */
    public ImportHandlerResult error(String msg) {
        this.ok = false;
        this.msg = msg;
        return this;
    }
}
