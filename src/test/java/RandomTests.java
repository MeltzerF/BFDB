import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Evgeniy Slobozheniuk on 23.04.2018.
 */
public class RandomTests {
    private static final Logger log = LogManager.getLogger(RandomTests.class);
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    ExecutorService executorService = Executors.newCachedThreadPool();
    @Test
    public void testTwoExecutorServices() {
        List<String> list = new ArrayList<>();
        list.add("Test 1");
        list.add("Test 2");
        list.add("Test 3");
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            list.forEach(x -> executorService.execute(() -> {
                log.info(x);
            }));
        }, 0, 2, TimeUnit.SECONDS);
        while (true) {

        }
    }
}
