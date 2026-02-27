package common;

import java.io.Serializable;

/**
 * Task interface - all client task classes must implement this.
 * Serializable so tasks can be passed over the network via RMI.
 *
 * DIARY: Day 1 - Created this interface as the base contract for all tasks.
 *        Every task (Add, Update, Delete, Calculate, Receipt) implements this.
 */
public interface Task<T> extends Serializable {

    /**
     * Execute the task logic.
     * @return result of type T (String message, cost value, receipt, etc.)
     */
    T execute();
}
