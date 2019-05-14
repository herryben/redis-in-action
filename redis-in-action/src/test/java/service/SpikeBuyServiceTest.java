package service;

import com.google.common.util.concurrent.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SpikeBuyServiceTest {
    @Test
    public void testBuy() throws InterruptedException {
        final SpikeBuyService service = new SpikeBuyService();
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        for (int i = 0; i < 1000; i++) {
            final int finalI = i;
            ListenableFuture explosion = executorService.submit(new Callable() {
                public Object call() throws Exception {
                    int res;
                    while ((res = service.buy(finalI, 10001, (int) (Math.random() * 5))) == 0) {
                        TimeUnit.MILLISECONDS.sleep(3);
                    }
                    return new ImmutablePair<Integer, Integer>(finalI, res);
                }
            });
            Futures.addCallback(explosion, new FutureCallback() {
                public void onSuccess(Object result) {
                    ImmutablePair rs = (ImmutablePair) result;
                    System.out.println("buyer " + rs.getLeft() + " buy " + rs.getRight());
                }

                public void onFailure(Throwable t) {
                    System.out.println(t.getMessage());
                }
            }, executorService);
        }
        TimeUnit.DAYS.sleep(1);
    }
}
