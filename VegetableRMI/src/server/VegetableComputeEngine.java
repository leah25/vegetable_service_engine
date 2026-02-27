package server;

import common.Compute;
import common.Task;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * VegetableComputeEngine - the RMI server.
 *
 * - Extends UnicastRemoteObject so Java RMI can export it automatically.
 * - Implements Compute so clients can call executeTask() remotely.
 * - Registers itself in the RMI Registry under the name "VegetableEngine".
 * - main() starts the registry on port 1099 and binds the engine.
 *
 * DIARY: Day 2 - Created the engine class.
 *        Extending UnicastRemoteObject is the easiest way to export;
 *        the constructor must call super() which can throw RemoteException.
 *        Used LocateRegistry.createRegistry(1099) so we don't need to
 *        run the external rmiregistry tool separately.
 */
public class VegetableComputeEngine extends UnicastRemoteObject implements Compute {

    private static final long serialVersionUID = 1L;

    // Name under which the engine is registered in the RMI registry
    public static final String REGISTRY_NAME = "VegetableEngine";

    /**
     * Constructor must call super() to properly export the remote object.
     * @throws RemoteException required by UnicastRemoteObject
     */
    public VegetableComputeEngine() throws RemoteException {
        super(); // exports this object on an anonymous port
        System.out.println("[Engine] VegetableComputeEngine created.");
    }

    /**
     * Execute any Task sent by a client.
     * The task object carries all data needed; the engine just calls execute().
     *
     * @param task the client task to run
     * @return result produced by the task
     * @throws RemoteException if RMI communication fails
     */
    @Override
    public <T> T executeTask(Task<T> task) throws RemoteException {
        System.out.println("[Engine] Received task: " + task.getClass().getSimpleName());
        T result = task.execute();
        System.out.println("[Engine] Task completed. Result: " + result);
        return result;
    }

    // ── Server startup ────────────────────────────────────────────────────────

    /**
     * Start the RMI registry and bind the engine.
     * Run this class first before any clients connect.
     */
    public static void main(String[] args) {
        try {
            // Create (or reuse) RMI registry on default port 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("[Server] RMI Registry started on port 1099.");

            // Create the engine instance
            VegetableComputeEngine engine = new VegetableComputeEngine();

            // Bind the engine in the registry
            registry.rebind(REGISTRY_NAME, engine);
            System.out.println("[Server] VegetableComputeEngine bound as '" + REGISTRY_NAME + "'.");
            System.out.println("[Server] Server is ready and waiting for clients...");
            System.out.println("[Server] Pre-loaded vegetable table:");
            System.out.println(VegetablePriceTable.getInstance().printTable());

        } catch (RemoteException e) {
            System.err.println("[Server] ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
