package service;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LimiterTest {
    @Test
    public void testLimiter() throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            service.submit(() ->{
                System.out.println("res " + LimiterService.limit("test", 5, 60));
            });
        }
        TimeUnit.SECONDS.sleep(5);
    }
}
