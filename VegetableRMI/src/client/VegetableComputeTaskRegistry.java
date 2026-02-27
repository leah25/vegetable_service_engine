package client;

import common.Compute;
import common.Task;
import common.VegetablePrice;
import server.VegetableComputeEngine;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * VegetableComputeTaskRegistry - the RMI client.
 *
 * - Looks up the VegetableComputeEngine in the RMI registry.
 * - Creates each of the 5 client task types.
 * - Sends each task to the server via executeTask() and prints the result.
 *
 * DIARY: Day 3 - Created the client main class.
 *        LocateRegistry.getRegistry("localhost", 1099) finds the server.
 *        registry.lookup(name) returns the remote stub.
 *        We cast the stub to Compute and call executeTask().
 *        Added a simple menu so all 5 tasks can be tested interactively.
 */
public class VegetableComputeTaskRegistry {

    private static Compute engine; // remote stub

    public static void main(String[] args) {
        System.out.println("=== Vegetable Service Engine - Client ===");

        // ── Step 1: Connect to RMI registry ──────────────────────────────────
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            engine = (Compute) registry.lookup(VegetableComputeEngine.REGISTRY_NAME);
            System.out.println("[Client] Connected to VegetableComputeEngine on localhost:1099\n");
        } catch (RemoteException | NotBoundException e) {
            System.err.println("[Client] Could not connect to server: " + e.getMessage());
            System.err.println("[Client] Make sure VegetableComputeEngine.main() is running first.");
            return;
        }

        // ── Step 2: Interactive menu ──────────────────────────────────────────
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n========= MENU =========");
            System.out.println("1. Add a vegetable price");
            System.out.println("2. Update a vegetable price");
            System.out.println("3. Delete a vegetable price");
            System.out.println("4. Calculate vegetable cost");
            System.out.println("5. Print receipt");
            System.out.println("6. Run all demo tasks automatically");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> runAddTask(sc);
                case "2" -> runUpdateTask(sc);
                case "3" -> runDeleteTask(sc);
                case "4" -> runCalCostTask(sc);
                case "5" -> runReceiptTask(sc);
                case "6" -> runAllDemoTasks();
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice. Try again.");
            }
        }

        sc.close();
        System.out.println("[Client] Disconnected. Goodbye.");
    }

    // ── Task 1: Add ───────────────────────────────────────────────────────────

    private static void runAddTask(Scanner sc) {
        System.out.print("Enter new vegetable ID (e.g. V006): ");
        String id = sc.nextLine().trim();
        System.out.print("Enter vegetable name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter price per kg (KES): ");
        double price = Double.parseDouble(sc.nextLine().trim());

        runTask(new AddVegetablePrice(new VegetablePrice(id, name, price)));
    }

    // ── Task 2: Update ────────────────────────────────────────────────────────

    private static void runUpdateTask(Scanner sc) {
        System.out.print("Enter vegetable ID to update: ");
        String id = sc.nextLine().trim();
        System.out.print("Enter new name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter new price per kg (KES): ");
        double price = Double.parseDouble(sc.nextLine().trim());

        runTask(new UpdateVegetablePrice(new VegetablePrice(id, name, price)));
    }

    // ── Task 3: Delete ────────────────────────────────────────────────────────

    private static void runDeleteTask(Scanner sc) {
        System.out.print("Enter vegetable ID to delete: ");
        String id = sc.nextLine().trim();
        runTask(new DeleteVegetablePrice(id));
    }

    // ── Task 4: Calculate cost ────────────────────────────────────────────────

    private static void runCalCostTask(Scanner sc) {
        System.out.print("Enter vegetable ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Enter quantity in kg: ");
        double qty = Double.parseDouble(sc.nextLine().trim());
        runTask(new CalVegetableCost(id, qty));
    }

    // ── Task 5: Print receipt ─────────────────────────────────────────────────

    private static void runReceiptTask(Scanner sc) {
        System.out.print("Enter cashier name: ");
        String cashier = sc.nextLine().trim();

        Map<String, Double> items = new LinkedHashMap<>();
        System.out.println("Enter items (type 'done' when finished):");

        while (true) {
            System.out.print("  Vegetable ID (or 'done'): ");
            String id = sc.nextLine().trim();
            if (id.equalsIgnoreCase("done")) break;
            System.out.print("  Quantity (kg): ");
            double qty = Double.parseDouble(sc.nextLine().trim());
            items.put(id, qty);
        }

        System.out.print("Amount given by customer (KES): ");
        double given = Double.parseDouble(sc.nextLine().trim());

        runTask(new CalculateCost(items, given, cashier));
    }

    // ── Demo: Run all 5 tasks automatically with sample data ──────────────────

    private static void runAllDemoTasks() {
        System.out.println("\n--- DEMO: Running all 5 tasks with sample data ---\n");

        // Task 1 - Add
        System.out.println("[TASK 1] Add new vegetable: Broccoli V006 @ KES 80/kg");
        runTask(new AddVegetablePrice(new VegetablePrice("V006", "Broccoli", 80.00)));

        // Task 2 - Update
        System.out.println("\n[TASK 2] Update Tomato (V001) price from 60 to 75 KES/kg");
        runTask(new UpdateVegetablePrice(new VegetablePrice("V001", "Tomato", 75.00)));

        // Task 3 - Delete
        System.out.println("\n[TASK 3] Delete Spinach (V003)");
        runTask(new DeleteVegetablePrice("V003"));

        // Task 4 - Calculate cost
        System.out.println("\n[TASK 4] Calculate cost of 3.5 kg of Carrot (V002)");
        runTask(new CalVegetableCost("V002", 3.5));

        // Task 5 - Receipt
        System.out.println("\n[TASK 5] Print receipt for cashier 'Alice'");
        Map<String, Double> cart = new LinkedHashMap<>();
        cart.put("V001", 2.0);  // 2 kg Tomato
        cart.put("V002", 1.5);  // 1.5 kg Carrot
        cart.put("V004", 1.0);  // 1 kg Onion
        runTask(new CalculateCost(cart, 500.00, "Alice"));
    }

    // ── Helper: send task to server and print result ──────────────────────────

    private static <T> void runTask(Task<T> task) {
        try {
            T result = engine.executeTask(task);
            System.out.println("\n[RESULT]\n" + result);
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
        }
    }
}
