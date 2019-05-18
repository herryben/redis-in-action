package service;

import redis.clients.jedis.Jedis;
import util.JedisUtil;

import java.util.Collections;

public class DedLockService {
    private static final String SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final String SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then \n" +
            "return redis.call('del', KEYS[1]) \n" +
            "else \n" +
            "return 0 \n" +
            "end";

    public static boolean lock(String lockKey, String requestId, int expireTime){
        Jedis jedis = JedisUtil.getJedis();
        try {
            return SUCCESS.equals(jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime));
        } finally {
            JedisUtil.returnJedis(jedis);
        }
    }

    public static boolean unLock(String lockKey, String requestId){
        Jedis jedis = JedisUtil.getJedis();
        try {
            return 1 == (Integer) jedis.eval(SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        } finally {
            JedisUtil.returnJedis(jedis);
        }
    }
}
