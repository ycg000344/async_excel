package cn.async.excel.properties;


import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "async.excel")
public class AsyncExcelProperties {

    private String baseDir = StrUtil.EMPTY;

}
