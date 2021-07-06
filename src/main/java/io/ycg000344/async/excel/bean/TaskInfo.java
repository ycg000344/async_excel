package io.ycg000344.async.excel.bean;

import io.ycg000344.async.excel.constant.ParseEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskInfo {

    private String taskId;
    private ParseEnum parseEnum;
    private String fileName;
    private String sourceFilePath;
    private String downloadFilePath;

}
