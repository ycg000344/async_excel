package cn.async.excel.bean;

import cn.async.excel.constant.ParseEnum;
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
