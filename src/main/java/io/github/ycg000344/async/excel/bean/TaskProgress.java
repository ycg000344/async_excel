package io.github.ycg000344.async.excel.bean;

import io.github.ycg000344.async.excel.util.AsyncExcelUtils;
import cn.hutool.json.JSONUtil;
import lombok.Builder;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Builder
public class TaskProgress {

    private TaskInfo taskInfo;
    private Double percent;
    private Integer total;
    private Integer error;
    private Integer success;


    public void update(RedisTemplate redisTemplate) {
        redisTemplate.opsForValue().set(AsyncExcelUtils.taskProgressRedisKey(this.taskInfo.getTaskId()), value(), timeout());
    }

    private Duration timeout() {
        return Duration.ofHours(1l);
    }

    private String value() {
        return JSONUtil.parse(this).toString();
    }
}
