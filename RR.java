import java.util.LinkedList;
import java.util.Queue;

/**
 * Implementation of the Round-Robin (RR) Scheduling Algorithm
 */
public class RR implements Scheduler {

    // Queue to store processes waiting to be scheduled
    private Queue<SimProcess> processQueue = new LinkedList<>();
    // Currently running process, if any
    private SimProcess currentProcess = null;
    // Time quantum for the Round-Robin algorithm
    private int quantum;
    // Remaining time for the current process in the current quantum
    private int timeLeftForCurrentProcess;

    // Constructor to initialize the quantum
    public RR(int quantum) {
        this.quantum = quantum; 
    }

    /**
     * Handles the arrival of a new process into the system.
     * @param p The process that has arrived.
     * @param time The current time of the simulation.
     */
    @Override
    public void onProcessArrival(SimProcess p, int time) {
        // Add the new process to the queue
        processQueue.add(p);

        // If no process is currently running, start running the newly arrived process
        if (currentProcess == null) {
            currentProcess = processQueue.poll(); // Get the next process from the queue
            timeLeftForCurrentProcess = Math.min(currentProcess.getBurstTime(), quantum);
        }
    }

    /**
     * Handles the completion of the currently running process.
     * @param p The process that has finished.
     * @param time The current time of the simulation.
     */
    @Override
    public void onProcessExit(SimProcess p, int time) {
        // Clear the current process
        currentProcess = null;

        // Start the next process from the queue, if any
        if (!processQueue.isEmpty()) {
            currentProcess = processQueue.poll();
            timeLeftForCurrentProcess = Math.min(currentProcess.getBurstTime(), quantum);
        }
    }

    /**
     * Simulates the behavior of the CPU at each clock interrupt.
     * @param timeElapsed The time elapsed since the last interrupt.
     * @param time The current time of the simulation.
     */
    @Override
    public void onClockInterrupt(int timeElapsed, int time) {
        // If no process is running, do nothing
        if (currentProcess == null) {
            return;
        }

        // Decrease the remaining time for the current process in this quantum
        timeLeftForCurrentProcess--;

        // Check if the current process has finished execution
        if (currentProcess.getBurstTime() == 0) {
            onProcessExit(currentProcess, time); // Mark process as completed
        } 
        // If the current process's quantum has expired
        else if (timeLeftForCurrentProcess == 0) {
            processQueue.add(currentProcess); // Re-queue the process
            currentProcess = null; // Set current process to null

            // Start the next process in the queue, if any
            if (!processQueue.isEmpty()) {
                currentProcess = processQueue.poll();
                timeLeftForCurrentProcess = Math.min(currentProcess.getBurstTime(), quantum);
            }
        }
    }

    /**
     * Returns the name of the scheduling algorithm.
     * @return The name "Round-Robin".
     */
    @Override
    public String getAlgorithmName() {
        return "Round-Robin";
    }

    /**
     * Returns the currently running process.
     * @return The current process, or null if no process is running.
     */
    @Override
    public SimProcess currentProcess() {
        return currentProcess;
    }

    /**
     * Checks if the system has no processes to execute.
     * @return True if there are no processes in the queue or running; otherwise, false.
     */
    @Override
    public boolean isEmpty() {
        return currentProcess == null && processQueue.isEmpty();
    }
}
