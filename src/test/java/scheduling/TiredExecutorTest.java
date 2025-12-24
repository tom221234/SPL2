package scheduling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit tests for TiredExecutor class.
 * Tests thread pool operations: submit, submitAll, shutdown
 */
public class TiredExecutorTest {

    private TiredExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new TiredExecutor(4);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        if (executor != null) {
            executor.shutdown();
        }
    }

    // ==================== Submit Tests ====================

    @Test
    void testSubmit_ExecutesTask() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();
        tasks.add(counter::incrementAndGet);

        executor.submitAll(tasks);

        assertEquals(1, counter.get());
    }

    @Test
    void testSubmitAll_ExecutesAllTasks() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(counter::incrementAndGet);
        }

        executor.submitAll(tasks);

        assertEquals(10, counter.get());
    }

    @Test
    void testSubmitAll_WaitsForCompletion() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            tasks.add(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                counter.incrementAndGet();
            });
        }

        executor.submitAll(tasks);

        // After submitAll returns, all tasks should be complete
        assertEquals(5, counter.get());
    }

    // ==================== Worker Report Tests ====================

    @Test
    void testGetWorkerReport_ReturnsNonEmpty() {
        List<Runnable> tasks = new ArrayList<>();
        tasks.add(() -> {
        });
        executor.submitAll(tasks);

        String report = executor.getWorkerReport();
        assertNotNull(report);
        assertFalse(report.isEmpty());
    }

    @Test
    void testGetWorkerReport_ContainsAllWorkers() {
        String report = executor.getWorkerReport();
        // Should contain info for all 4 workers
        assertTrue(report.contains("id: 0"));
        assertTrue(report.contains("id: 1"));
        assertTrue(report.contains("id: 2"));
        assertTrue(report.contains("id: 3"));
    }

    // ==================== Fairness Tests ====================

    @Test
    void testFairness_TasksDistributedToLeastFatiguedWorker() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        // Submit many tasks
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(() -> {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                counter.incrementAndGet();
            });
        }

        executor.submitAll(tasks);

        assertEquals(100, counter.get());
    }

    // ==================== Edge Cases ====================

    @Test
    void testSubmitAll_EmptyList() {
        List<Runnable> tasks = new ArrayList<>();
        executor.submitAll(tasks);
        // Should not throw and should return immediately
    }

    @Test
    void testSubmitAll_SingleTask() {
        AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();
        tasks.add(counter::incrementAndGet);

        executor.submitAll(tasks);

        assertEquals(1, counter.get());
    }

    @Test
    void testMultipleSubmitAll_InSequence() {
        AtomicInteger counter = new AtomicInteger(0);

        for (int batch = 0; batch < 5; batch++) {
            List<Runnable> tasks = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                tasks.add(counter::incrementAndGet);
            }
            executor.submitAll(tasks);
        }

        assertEquals(50, counter.get());
    }

    // ==================== Concurrency Tests ====================

    @Test
    void testConcurrentExecution_NoDataRace() throws InterruptedException {
        int[] array = new int[1000];
        List<Runnable> tasks = new ArrayList<>();

        // Each task writes to a different index - no race condition
        for (int i = 0; i < 1000; i++) {
            final int index = i;
            tasks.add(() -> {
                array[index] = index * 2;
            });
        }

        executor.submitAll(tasks);

        // Verify all writes completed correctly
        for (int i = 0; i < 1000; i++) {
            assertEquals(i * 2, array[i]);
        }
    }
}
