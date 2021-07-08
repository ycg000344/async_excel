package cn.async.excel.manager;

import cn.async.excel.properties.AsyncExcelProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
