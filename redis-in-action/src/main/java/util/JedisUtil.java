package util;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;

public class JedisUtil {
    private JedisUtil() {
    }
    private static JedisPool jedisPool = null;
    static {
        Properties properties = PropertyUtil.loadProperties("redis.properties");
        String host = properties.getProperty("redis.host");
        String maxIdle = properties.getProperty("redis.maxIdle");
        String maxWaitMills = properties.getProperty("redis.maxWaitMills");
        String testOnBorrow = properties.getProperty("redis.testOnBorrow");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(NumberUtils.toInt(maxIdle));
        config.setMaxWaitMillis(NumberUtils.toLong(maxWaitMills));
        config.setTestOnBorrow(BooleanUtils.toBoolean(testOnBorrow));
        config.setMaxTotal(2000);
        jedisPool = new JedisPool(config, host);
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static void returnJedis(Jedis jedis) {
        if (jedis != null && jedisPool != null) {
            jedisPool.returnResource(jedis);
        }
    }
}
