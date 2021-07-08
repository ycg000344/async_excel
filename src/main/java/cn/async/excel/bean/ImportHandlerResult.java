package cn.async.excel.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportHandlerResult {

    private boolean ok;
    private String msg;

    public ImportHandlerResult error(String msg) {
        this.ok = false;
        this.msg = msg;
        return this;
    }
}
