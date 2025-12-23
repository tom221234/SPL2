package scheduling;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TiredThread extends Thread implements Comparable<TiredThread> {

    private static final Runnable POISON_PILL = () -> {}; // Special task to signal shutdown

    private final int id; // Worker index assigned by the executor
    private final double fatigueFactor; // Multiplier for fatigue calculation

    private final AtomicBoolean alive = new AtomicBoolean(true); // Indicates if the worker should keep running

    // Single-slot handoff queue; executor will put tasks here
    private final BlockingQueue<Runnable> handoff = new ArrayBlockingQueue<>(1);

    private final AtomicBoolean busy = new AtomicBoolean(false); // Indicates if the worker is currently executing a task

    private final AtomicLong timeUsed = new AtomicLong(0); // Total time spent executing tasks
    private final AtomicLong timeIdle = new AtomicLong(0); // Total time spent idle
    private final AtomicLong idleStartTime = new AtomicLong(0); // Timestamp when the worker became idle

    public TiredThread(int id, double fatigueFactor) {
        this.id = id;
        this.fatigueFactor = fatigueFactor;
        this.idleStartTime.set(System.nanoTime());
        setName(String.format("FF=%.2f", fatigueFactor));
    }

    public int getWorkerId() {
        return id;
    }

    public double getFatigue() {
        return fatigueFactor * timeUsed.get();
    }

    public boolean isBusy() {
        return busy.get();
    }

    public long getTimeUsed() {
        return timeUsed.get();
    }

    public long getTimeIdle() {
        return timeIdle.get();
    }

    /**
     * Assign a task to this worker.
     * This method is non-blocking: if the worker is not ready to accept a task,
     * it throws IllegalStateException.
     */
    public void newTask(Runnable task) {
       // TODO
        if(!this.alive.get() || this.busy.get() || task == null)
            throw new IllegalStateException("worker is not ready to accept a task");
        handoff.add(task);

    }

    /**
     * Request this worker to stop after finishing current task.
     * Inserts a poison pill so the worker wakes up and exits.
     */
    public void shutdown() {
       // TODO
        alive.set(false);
        busy.set(false);
        handoff.add(POISON_PILL);
    }

    @Override
    public void run() {
       // TODO
        while(this.alive.get()) {
            try
            {
                Runnable task = handoff.take();
                if (task == POISON_PILL) {
                    break;
                }
                this.busy.set(true);


                long idleEnd = System.nanoTime(); // Gets the time from the system for calculation of idle time
                timeIdle.addAndGet(idleEnd - idleStartTime.get());


                long workStart = System.nanoTime(); // Gets the time from the system and task has started running

                task.run();


                long workEnd = System.nanoTime(); // Gets the time from the system and task has stopped running


                timeUsed.addAndGet(workEnd - workStart); // Calculates time used (workEnd - workStart)
                busy.set(false);


                idleStartTime.set(System.nanoTime());  //start timer of idle
            }
            catch (InterruptedException e)
            {
                break;
            }


        }
    }

    @Override
    public int compareTo(TiredThread o) {
        // TODO
        return Double.compare(this.getFatigue(), o.getFatigue()); // return who has more fatigue
    }
}