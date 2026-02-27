#  MIT8102 Assignment #1
# Vegetable Service Engine – RMI Implementation
# ================================================

## DAY 1 – Project Setup & Design

**Goal:** Understand RMI architecture and plan the class structure.

**Steps taken:**
1. Read Oracle Java RMI tutorial and Baeldung RMI guide.
2. Decided on package structure:
   - `common/`  – shared interfaces and models (Task, Compute, VegetablePrice)
   - `server/`  – VegetableComputeEngine + VegetablePriceTable
   - `client/`  – all 5 task classes + VegetableComputeTaskRegistry
   - `servlets/` – HTTP servlets wrapping the RMI tasks

3. Created `Task<T>` interface extending Serializable.
   - WHY: Tasks travel over the network via Java object serialization.
     Without Serializable, RMI would throw NotSerializableException.

4. Created `Compute` interface extending Remote.
   - WHY: This is the remote interface. The client only ever interacts
     with this interface, never the concrete engine class directly.
   - All methods declare RemoteException (RMI requirement).

5. Created `VegetablePrice` model (Serializable).
   - Fields: id, name, pricePerKg.
   - Pre-loaded 5 vegetables in VegetablePriceTable as sample data.

**Issues encountered:**
- Needed to decide between in-memory HashMap vs. SQLite DB.
  Chose HashMap as the assignment does not require persistence.

---

## DAY 2 – Server and Task Classes

**Goal:** Build the server engine and the first three task classes.

**Steps taken:**
1. Created `VegetablePriceTable` as a Singleton.
   - WHY: All tasks on the server share the same table instance.
     Singleton ensures only one copy exists in JVM memory.
   - Made all methods `synchronized` to prevent race conditions
     if multiple clients connect simultaneously.

2. Created `VegetableComputeEngine`:
   - Extends `UnicastRemoteObject` – this exports the object
     automatically on an anonymous port.
   - Constructor calls `super()` – required by UnicastRemoteObject,
     which is why the constructor declares `throws RemoteException`.
   - `executeTask()` simply calls `task.execute()` on whatever task
     the client sends – the engine doesn't need to know the task type.
   - `main()` calls `LocateRegistry.createRegistry(1099)` so we don't
     need to start the external `rmiregistry` tool separately.
   - Used `registry.rebind()` instead of `bind()` to avoid
     AlreadyBoundException on server restart.

3. Created `AddVegetablePrice`, `UpdateVegetablePrice`, `DeleteVegetablePrice`.
   - Each holds the data it needs as a field (set in constructor).
   - `execute()` runs on the SERVER side (the task travels there via RMI).
   - All return a descriptive String so the client can print feedback.

**Test:** Compiled server classes. Started engine, confirmed:
   "[Server] RMI Registry started on port 1099."

---

## DAY 3 – Cost and Receipt Tasks + Client

**Goal:** Implement Tasks 4 & 5 and the client main program.

**Steps taken:**
1. Created `CalVegetableCost`:
   - Holds vegetableId + quantityKg.
   - `execute()` looks up price, multiplies by qty, returns formatted string.

2. Created `CalculateCost` (receipt):
   - Holds a Map<String, Double> of items (id -> qty), amountGiven, cashierName.
   - `execute()` iterates items, builds a receipt with line totals,
     grand total, change due, and cashier name.
   - Used `LocalDateTime.now()` for timestamp.

3. Created `VegetableComputeTaskRegistry` (client main):
   - `LocateRegistry.getRegistry("localhost", 1099)` connects to server.
   - `registry.lookup("VegetableEngine")` returns the remote stub (Compute).
   - Cast to `Compute` and call `executeTask(task)`.
   - Added interactive menu + "Run all demo tasks" option for testing.

**Test:** Ran server in one terminal, client in another.
   All 5 tasks executed successfully with demo data.

**Issues encountered:**
- First run got `ClassNotFoundException` on server.
  FIX: Ensured both client and server had common/ classes on classpath.

---

## DAY 4 – Servlet Layer

**Goal:** Add HTTP servlets so a mobile app can call the engine over HTTP.

**Steps taken:**
1. Created one servlet per task:
   - `AddVegetableServlet`    → POST /vegetable/add
   - `UpdateVegetableServlet` → POST /vegetable/update
   - `DeleteVegetableServlet` → POST /vegetable/delete
   - `CalCostServlet`         → POST /vegetable/cost
   - `ReceiptServlet`         → POST /vegetable/receipt

2. Each servlet:
   a. Reads HTTP parameters from the request.
   b. Gets a Compute stub from the RMI registry.
   c. Creates the appropriate Task object.
   d. Calls engine.executeTask(task).
   e. Writes the String result back as HTTP plain text response.

3. Used `@WebServlet` annotation (no web.xml needed).

**Test:** Deployed to embedded Tomcat in IntelliJ.
   Tested endpoints with Postman / browser.

---

## HOW TO RUN (IntelliJ)

### Project setup:
1. File > New Project > Java (NOT Maven for simplicity)
2. Create packages: common, server, client, servlets
3. set up all .java files into their respective packages
4. Add javax.servlet-api.jar to Project Structure > Libraries
   (download from https://mvnrepository.com/artifact/javax.servlet/javax.servlet-api)

### Run the SERVER:
1. Right-click VegetableComputeEngine.java > Run 'VegetableComputeEngine.main()'
2. You should see:
   "[Server] RMI Registry started on port 1099."
   "[Server] VegetableComputeEngine bound as 'VegetableEngine'."

### Run the CLIENT (same machine or different):
1. Right-click VegetableComputeTaskRegistry.java > Run 'VegetableComputeTaskRegistry.main()'
2. The interactive menu will appear.
3. Choose option 6 to run all 5 demo tasks automatically.

### Run on DIFFERENT computers:
1. On computer A (server): run VegetableComputeEngine.main()
2. On computer B (client): change "localhost" to computer A's IP address
   in VegetableComputeTaskRegistry.java line:
     Registry registry = LocateRegistry.getRegistry("192.168.x.x", 1099);
3. Ensure port 1099 is open in the firewall on computer A.

### Run Servlets (optional):
1. Add Tomcat to IntelliJ: Run > Edit Configurations > + > Tomcat Server
2. Deploy the project as a WAR artifact.
3. Start Tomcat and test endpoints with Postman.

---

## RUN ON COMMAND LINE

- This option is for when computers are on different networks. You can still communicate with server:

---> curl -X POST "http://localhost:8080/VegetableRMI/vegetable/add" \
  -d "id=1&name=Tomato&price=2.50"

  see images below:

<img width="597" height="779" alt="image" src="https://github.com/user-attachments/assets/065de1aa-23f0-4f1b-9d80-3a407e536748" />


