package io.github.ycg000344.async.excel.manager;

import io.github.ycg000344.async.excel.properties.AsyncExcelProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lusheng
 * @since 2021-07-09
 */
@Configuration
@EnableConfigurationProperties(AsyncExcelProperties.class)
@Slf4j
public class AsyncExcelAutoConfig {

    @Bean
    public AsyncExcelTaskManager asyncExcelTaskManager(AsyncExcelProperties asyncExcelProperties) {
        AsyncExcelTaskManager manager = new AsyncExcelTaskManager();
        manager.afterPropertiesSet(asyncExcelProperties);
        log.info(">>> initial AsyncExcelTaskManager finish.");
        return manager;
    }

}
