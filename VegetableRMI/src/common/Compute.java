package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Compute interface - defines the remote method that the server exposes.
 * The client calls executeTask() over the network via RMI.
 *
 * DIARY: Day 1 - Created the RMI remote interface.
 *        This extends Remote so Java RMI knows it is a remote object.
 *        All methods must declare RemoteException.
 */
public interface Compute extends Remote {

    /**
     * Execute a given task on the server and return the result.
     * @param task any task object implementing the Task interface
     * @return result produced by the task
     * @throws RemoteException if RMI communication fails
     */
    <T> T executeTask(Task<T> task) throws RemoteException;
}
