import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Shortest Remaining Time First.
 */
public class SRTF implements Scheduler {

    private PriorityQueue<SimProcess> readyQueue;
    private SimProcess currentProcess;
    private Map<SimProcess, Integer> remainingTimeMap; // Tracks remaining time for each process

    public SRTF() {
        // Priority queue sorted by remaining time (shortest first)
        readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> remainingTimeMap.get(p)));
        remainingTimeMap = new HashMap<>();
        currentProcess = null;
    }

    @Override
    public void onProcessArrival(SimProcess p, int time) {
        // Initialize the remaining time for the new process
        remainingTimeMap.put(p, p.getBurstTime());
        readyQueue.add(p);

        // Check if the new process should preempt the current process
        if (currentProcess == null || remainingTimeMap.get(p) < remainingTimeMap.get(currentProcess)) {
            if (currentProcess != null) {
                readyQueue.add(currentProcess); // Add current process back to the queue
            }
            currentProcess = readyQueue.poll(); // Start the process with the shortest remaining time
        }
    }

    @Override
    public void onProcessExit(SimProcess p, int time) {
        remainingTimeMap.remove(p); // Remove the completed process
        currentProcess = null;

        if (!readyQueue.isEmpty()) {
            // Pick the next process with the shortest remaining time
            currentProcess = readyQueue.poll();
        }
    }

    @Override
    public void onClockInterrupt(int timeElapsed, int time) {
        if (currentProcess == null) {
            return;
        }

        // Decrement the remaining time of the current process
        int remainingTime = remainingTimeMap.get(currentProcess) - 1;
        remainingTimeMap.put(currentProcess, remainingTime);

        // If the current process completes, exit it
        if (remainingTime == 0) {
            onProcessExit(currentProcess, time);
        } else {
            // Check for preemption
            if (!readyQueue.isEmpty() && remainingTimeMap.get(readyQueue.peek()) < remainingTime) {
                readyQueue.add(currentProcess); // Add the current process back to the queue
                currentProcess = readyQueue.poll(); // Start the process with the shortest remaining time
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "Shortest Remaining Time First";
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
