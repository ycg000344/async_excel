package io.github.ycg000344.async.excel.bean;

import io.github.ycg000344.async.excel.constant.ParseEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @author lusheng
 * @since 2021-07-09
 */
@Data
@Builder
public class TaskInfo {

    private String taskId;
    private ParseEnum parseEnum;
    private String fileName;
    private String sourceFilePath;
    private String downloadFilePath;

}
