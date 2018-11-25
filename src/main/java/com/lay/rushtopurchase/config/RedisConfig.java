package com.lay.rushtopurchase.config;

import com.lay.rushtopurchase.utils.FastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;

/**
 * @Description:
 * @Author: lay
 * @Date: Created in 19:56 2018/11/25
 * @Modified By:IntelliJ IDEA
 */
@Configuration
public class RedisConfig {
    // redisTemplate
    @Autowired
    private RedisTemplate redisTemplate = null;

    @PostConstruct
    public void init() {
        initRedisTemplate();
    }

    public RedisTemplate initRedisTemplate() {
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        FastJsonRedisSerializer jacksonSeial=new FastJsonRedisSerializer<>(Object.class);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jacksonSeial);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jacksonSeial);
        return redisTemplate;
    }
}
