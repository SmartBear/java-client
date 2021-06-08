package io.split.engine.segments;

import io.split.client.SplitClientConfig;
import io.split.engine.SDKReadinessGates;
import io.split.cache.SegmentCache;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Tests for SegmentSynchronizationTaskImp
 *
 * @author adil+
 */
public class SegmentSynchronizationTaskImpTest {
    private static final Logger _log = LoggerFactory.getLogger(SegmentSynchronizationTaskImpTest.class);

    private AtomicReference<SegmentFetcher> fetcher1 = null;
    private AtomicReference<SegmentFetcher> fetcher2 = null;

    @Before
    public void beforeMethod() {
        fetcher1 = new AtomicReference<>(null);
        fetcher2 = new AtomicReference<>(null);
    }

    @Test
    public void works() {
        SDKReadinessGates gates = new SDKReadinessGates();
        SegmentCache segmentCache = Mockito.mock(SegmentCache.class);

        SegmentChangeFetcher segmentChangeFetcher = Mockito.mock(SegmentChangeFetcher.class);
        final SegmentSynchronizationTaskImp fetchers = new SegmentSynchronizationTaskImp(segmentChangeFetcher, 1L, 1, gates, segmentCache, SplitClientConfig.builder().build());


        // create two tasks that will separately call segment and make sure
        // that both of them get the exact same instance.
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                fetcher1.set(fetchers.getFetcher("foo"));
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                fetcher2.set(fetchers.getFetcher("foo"));
            }
        });

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10L, TimeUnit.SECONDS)) {
                _log.info("Executor did not terminate in the specified time.");
                List<Runnable> droppedTasks = executorService.shutdownNow();
                _log.info("Executor was abruptly shut down. These tasks will not be executed: " + droppedTasks);
            }
        } catch (InterruptedException e) {
            // reset the interrupt.
            Thread.currentThread().interrupt();
        }

        gates.splitsAreReady();

        assertThat(fetcher1.get(), is(notNullValue()));
        assertThat(fetcher1.get(), is(sameInstance(fetcher2.get())));
    }


}
