package service;

import redis.clients.jedis.Jedis;
import util.JedisUtil;

import java.util.ArrayList;
import java.util.List;

public class SpikeBuyService {
    private final static String script = "local hasBuy = redis.call('sismember', 'users', ARGV[1]) \n" +
            "if hasBuy ~= 0 then \n" +
            "   return 0 \n" +
            "end \n" +
            "local skuNum = tonumber(redis.call('get', 'sku:'..ARGV[2])) or 0 \n" +
            "local num = math.min(ARGV[3], 5) or 0 \n" +
            "if skuNum == 0 or num > skuNum then \n" +
            "   return 0 \n" +
            "end \n" +
            "redis.call('decrby', 'sku:'..ARGV[2], num) \n" +
            "redis.call('lpush', 'order:'..ARGV[2], ARGV[1]..' '..ARGV[3]) \n" +
            "redis.call('sadd', 'users', ARGV[1]) \n" +
            "return num";

    public int buy(long uid, long sku, int num) {
        Jedis jedis = JedisUtil.getJedis();
        List<String> args =  new ArrayList<String>();
        args.add(String.valueOf(uid));
        args.add(String.valueOf(sku));
        args.add(String.valueOf(num));
        long res = (Long) jedis.eval(script, new ArrayList<String>(), args);
        JedisUtil.returnJedis(jedis);
        return (int) res;
    }
}
