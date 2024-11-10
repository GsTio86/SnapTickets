package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Optional<Object> get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    @Override
    public boolean tryLock(String key, Object value, long time) {
        boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
        return result;
    }

    @Override
    public boolean unlock(String key, Object value) {
        if (value.equals(redisTemplate.opsForValue().get(key))) { // 確認是否為自己的鎖
            boolean result = Boolean.TRUE.equals(redisTemplate.delete(key));
            return result;
        }
        return false;
    }
}
