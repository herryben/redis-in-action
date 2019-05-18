package service;

import com.google.common.collect.Lists;
import redis.clients.jedis.Jedis;
import util.JedisUtil;

import java.util.Collections;

public class LimiterService {
    private static final String LIMIT_KEY = "limit:";
    private static final String SCRIPT = "local times = tonumber(redis.call('get', KEYS[1])) or 1\n" +
            "if times >= tonumber(ARGV[1]) then \n" +
            "   return 0 \n" +
            "end \n" +
            "if times == 1 then \n" +
            "   redis.call('incr', KEYS[1]) \n" +
            "   redis.call('expire', KEYS[1], tonumber(ARGV[2])) \n" +
            "else \n" +
            "   redis.call('incr', KEYS[1]) \n" +
            "end \n" +
            "return 1";
    public static boolean limit(String key, long times, long expire){
        Jedis jedis = JedisUtil.getJedis();
        try {
            return (Long)jedis.eval(SCRIPT, Collections.singletonList(LIMIT_KEY + key), Lists.newArrayList(String.valueOf(times), String.valueOf(expire))) != 0;
        } finally {
            jedis.close();
        }
    }
}
