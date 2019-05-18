package service;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import util.JedisUtil;

import java.util.concurrent.*;

public class RedLockTest {
    @Test
    public void redLockTest() throws InterruptedException {
        ExecutorService service = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                120L, TimeUnit.SECONDS,
                new SynchronousQueue<>());
        ConcurrentHashMap<Integer, Integer> hashMap = new ConcurrentHashMap<>(3000);
        for (int i = 0; i < 2000; i++) {
            hashMap.put(i, 0);
            int finalI = i;
            service.submit(() ->{
                String uuid = String.valueOf(finalI);
                while (true) {
                    if (DedLockService.lock("lock:counter", uuid, 10000)) {
                        hashMap.put(finalI, 1);
                        Jedis jedis = JedisUtil.getJedis();
                        String res = jedis.get("counter");
                        int counter = res == null ? 0 : NumberUtils.toInt(res);
                        counter++;
                        jedis.set("counter", String.valueOf(counter));
                        System.out.println(finalI + " uuid " + uuid + " set counter " + counter);
                        JedisUtil.returnJedis(jedis);
                        DedLockService.unLock("lock:counter", uuid);
                        break;
                    } else {
                        try {
                            TimeUnit.MILLISECONDS.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            });
        }
        TimeUnit.MINUTES.sleep(3);
        for (int i = 0; i < 2000; i++) {
            if (hashMap.get(i) == 0) {
                System.out.println("double " + i);
            }
        }
        System.out.println("finish .........");
    }
}
