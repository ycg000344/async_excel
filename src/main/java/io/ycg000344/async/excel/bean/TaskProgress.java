package io.ycg000344.async.excel.bean;

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
        redisTemplate.opsForValue().set(key(), value(), timeout());
    }

    private Duration timeout() {
        return Duration.ofHours(1l);
    }

    private String key() {
        return String.format("async:excel:%s", this.taskInfo.getTaskId());
    }

    private String value() {
        return JSONUtil.parse(this).toString();
    }
}
