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
        int retries = 3;
        for (int i = 0; i < retries; i++) {
            try {
                Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(result)) {
                    return true;
                } else if (result == null) {
                    Thread.sleep(100);
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean unlock(String key, Object value) {
        try {
            if (value.equals(redisTemplate.opsForValue().get(key))) {
                Boolean result = redisTemplate.delete(key);
                if (result != null && result) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
