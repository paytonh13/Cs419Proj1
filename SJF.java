import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Shortest Job First.
 */
public class SJF implements Scheduler {

    private PriorityQueue<SimProcess> readyQueue;
    private SimProcess currentProcess;

    public SJF() {
        // Priority queue sorts by burst time (shortest first)
        readyQueue = new PriorityQueue<>(Comparator.comparingInt(SimProcess::getBurstTime));
        currentProcess = null;
    }

    @Override
    public void onProcessArrival(SimProcess p, int time) {
        readyQueue.add(p); // Add the process to the ready queue
        if (currentProcess == null) {
            // If no process is running, pick the shortest one
            currentProcess = readyQueue.poll();
        }
    }

    @Override
    public void onProcessExit(SimProcess p, int time) {
        currentProcess = null;
        if (!readyQueue.isEmpty()) {
            // Pick the next shortest job from the ready queue
            currentProcess = readyQueue.poll();
        }
    }

    @Override
    public void onClockInterrupt(int timeElapsed, int time) {
        // No action needed for non-preemptive scheduling
    }

    @Override
    public String getAlgorithmName() {
        return "Shortest Job First";
    }

    @Override
    public SimProcess currentProcess() {
        return currentProcess;
    }

    @Override
    public boolean isEmpty() {
        return currentProcess == null && readyQueue.isEmpty();
    }
}
