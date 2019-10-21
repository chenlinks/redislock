package com.redis.lock.tools;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;


/**
 * redis实现分布式锁
 */
public class JedisTool {

    private static final Long RELEASE_SUCCESS = 1L;
    private static final String LOCK_SUCCESS = "OK";


    /***
     *  获取分布式锁
     * @param jedis
     * @param lockKey
     * @param requestId
     * @param expireTime
     * @return
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, long expireTime){
        SetParams params = new SetParams();
        params.nx();
        params.px(expireTime);
        String result = jedis.set(lockKey, requestId, params);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }


    /**
     * 解锁
     * @param jedis
     * @param lockKey
     * @param requestId
     * @return
     */
    public  static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId){
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }



}
