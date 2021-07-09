package io.github.ycg000344.async.excel.properties;


import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lusheng
 * @since 2021-07-09
 */
@Data
@ConfigurationProperties(prefix = "async.excel")
public class AsyncExcelProperties {

    private String baseDir = StrUtil.EMPTY;

}
