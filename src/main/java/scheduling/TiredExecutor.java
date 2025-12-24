package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        workers = new TiredThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            double randomFatigue = 0.5 + Math.random();
            workers[i] = new TiredThread(i, randomFatigue);
            workers[i].start();
            idleMinHeap.add(workers[i]);
        }
    }

    public void submit(Runnable task) {
        // TODO
        inFlight.incrementAndGet();
        TiredThread worker;
        try {
            worker = idleMinHeap.take();  // take the most fresh thread
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        Runnable wrappedTask = () -> {  // wrapper for task
            try {
                task.run();
            } finally {
                idleMinHeap.add(worker);
                inFlight.decrementAndGet();
            }
        };
        worker.newTask(wrappedTask); // send the wrapped task to worker

    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for (Runnable task : tasks) { // submit all tasks for the threads
            this.submit(task);
        }
        while (inFlight.get() > 0) { // wait until all finish
            Thread.yield();
        }

    }

    public void shutdown() throws InterruptedException {
        // TODO
        for (TiredThread worker : workers) {
            worker.shutdown();
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        StringBuilder report = new StringBuilder();
        for ( TiredThread worker : workers ) {
            report.append("id: ").append(worker.getWorkerId())
            .append(" Fatigue: ").append((worker.getFatigue()))
            .append(" Time Used: ").append(worker.getTimeUsed())
            .append(" Time Idle: ").append(worker.getTimeIdle()).append("\n");
        }
        return report.toString();
    }
}
