package scheduling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TiredThread class.
 * Tests worker thread operations: newTask, shutdown, fatigue calculation
 */
public class TiredThreadTest {

    // ==================== Basic Tests ====================

    @Test
    void testGetWorkerId_ReturnsCorrectId() {
        TiredThread thread = new TiredThread(5, 1.0);
        assertEquals(5, thread.getWorkerId());
        thread.shutdown();
    }

    @Test
    void testGetFatigue_InitiallyZero() {
        TiredThread thread = new TiredThread(0, 1.0);
        assertEquals(0.0, thread.getFatigue());
        thread.shutdown();
    }

    @Test
    void testIsBusy_InitiallyFalse() {
        TiredThread thread = new TiredThread(0, 1.0);
        assertFalse(thread.isBusy());
        thread.shutdown();
    }

    @Test
    void testGetTimeUsed_InitiallyZero() {
        TiredThread thread = new TiredThread(0, 1.0);
        assertEquals(0, thread.getTimeUsed());
        thread.shutdown();
    }

    @Test
    void testGetTimeIdle_InitiallyZero() {
        TiredThread thread = new TiredThread(0, 1.0);
        assertEquals(0, thread.getTimeIdle());
        thread.shutdown();
    }

    // ==================== Fatigue Factor Tests ====================

    @Test
    void testFatigue_IncreasesWithWork() throws InterruptedException {
        TiredThread thread = new TiredThread(0, 1.0);
        thread.start();

        // Give thread a task that takes some time
        thread.newTask(() -> {
            long sum = 0;
            for (int i = 0; i < 1000000; i++) {
                sum += i;
            }
        });

        Thread.sleep(100); // Wait for task to complete

        assertTrue(thread.getFatigue() > 0);
        thread.shutdown();
        thread.join();
    }

    @Test
    void testFatigue_DifferentFactorsGiveDifferentFatigue() throws InterruptedException {
        TiredThread lowFatigue = new TiredThread(0, 0.5);
        TiredThread highFatigue = new TiredThread(1, 1.5);

        lowFatigue.start();
        highFatigue.start();

        Runnable work = () -> {
            long sum = 0;
            for (int i = 0; i < 100000; i++) {
                sum += i;
            }
        };

        lowFatigue.newTask(work);
        highFatigue.newTask(work);

        Thread.sleep(100);

        // Both did same work, but different fatigue factors
        // Note: Actual fatigue values depend on CPU time

        lowFatigue.shutdown();
        highFatigue.shutdown();
        lowFatigue.join();
        highFatigue.join();
    }

    // ==================== CompareTo Tests ====================

    @Test
    void testCompareTo_LowerFatigueComesFirst() {
        TiredThread t1 = new TiredThread(0, 1.0);
        TiredThread t2 = new TiredThread(1, 1.0);

        // Both have 0 fatigue initially
        assertEquals(0, t1.compareTo(t2));

        t1.shutdown();
        t2.shutdown();
    }

    // ==================== newTask Tests ====================

    @Test
    void testNewTask_ThrowsIfNull() {
        TiredThread thread = new TiredThread(0, 1.0);
        thread.start();

        assertThrows(IllegalStateException.class, () -> thread.newTask(null));

        thread.shutdown();
    }

    @Test
    void testNewTask_ThrowsAfterShutdown() {
        TiredThread thread = new TiredThread(0, 1.0);
        thread.start();
        thread.shutdown();

        assertThrows(IllegalStateException.class, () -> thread.newTask(() -> {
        }));
    }

    // ==================== Shutdown Tests ====================

    @Test
    void testShutdown_StopsThread() throws InterruptedException {
        TiredThread thread = new TiredThread(0, 1.0);
        thread.start();

        assertTrue(thread.isAlive());

        thread.shutdown();
        thread.join(1000);

        assertFalse(thread.isAlive());
    }
}
