package service;

import org.junit.Test;

public class SpikeBuyServiceTest {
    @Test
    public void testBuy() {
        SpikeBuyService service = new SpikeBuyService();
        System.out.println("res " + service.buy(2, 10001, 30));
    }
}
