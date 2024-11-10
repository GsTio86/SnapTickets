package me.gt.snaptickets.service;

import java.util.Optional;

public interface RedisService {

    /**
     * 設置key-value
     * @param key
     * @param value
     */
    void set(String key, Object value);


    /**
     * 取得key對應的value
     * @param key
     * @return optional value
     */
    Optional<Object> get(String key);

    /**
     * 上鎖
     *
     * @param key 鎖的key
     * @param value 鎖的值
     * @param time 鎖的過期時間
     * @return 是否上鎖成功
     */
    boolean tryLock(String key, Object value, long time);

    /**
     * 解鎖
     *
     * @param key 鎖的key
     * @param value 鎖的值
     * @return 是否解鎖成功
     */
    boolean unlock(String key, Object value);
}
