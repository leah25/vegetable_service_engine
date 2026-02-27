package server;

import common.VegetablePrice;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * VegetablePriceTable - acts as the in-memory database on the server.
 * Singleton pattern so all tasks share the same table instance.
 *
 * DIARY: Day 1 - Decided to use a static singleton HashMap as the
 *        "database" since the assignment does not require a real DB.
 *        Pre-loaded five vegetables so testing is immediate.
 */
public class VegetablePriceTable {

    // Singleton instance
    private static VegetablePriceTable instance;

    // The price table: vegetable ID -> VegetablePrice
    private final Map<String, VegetablePrice> table = new LinkedHashMap<>();

    // Private constructor - use getInstance()
    private VegetablePriceTable() {
        // Pre-load sample data
        table.put("V001", new VegetablePrice("V001", "Tomato",    60.00));
        table.put("V002", new VegetablePrice("V002", "Carrot",    45.00));
        table.put("V003", new VegetablePrice("V003", "Spinach",   30.00));
        table.put("V004", new VegetablePrice("V004", "Onion",     50.00));
        table.put("V005", new VegetablePrice("V005", "Cabbage",   25.00));
    }

    /** Return the single shared instance. */
    public static synchronized VegetablePriceTable getInstance() {
        if (instance == null) {
            instance = new VegetablePriceTable();
        }
        return instance;
    }

    // ── CRUD operations ──────────────────────────────────────────────────────

    /** Add a new vegetable. Returns false if ID already exists. */
    public synchronized boolean add(VegetablePrice vp) {
        if (table.containsKey(vp.getId())) return false;
        table.put(vp.getId(), vp);
        return true;
    }

    /** Update an existing vegetable. Returns false if not found. */
    public synchronized boolean update(VegetablePrice vp) {
        if (!table.containsKey(vp.getId())) return false;
        table.put(vp.getId(), vp);
        return true;
    }

    /** Delete a vegetable by ID. Returns false if not found. */
    public synchronized boolean delete(String id) {
        if (!table.containsKey(id)) return false;
        table.remove(id);
        return true;
    }

    /** Find a vegetable by ID. Returns null if not found. */
    public synchronized VegetablePrice find(String id) {
        return table.get(id);
    }

    /** Return all vegetables. */
    public synchronized Collection<VegetablePrice> getAll() {
        return table.values();
    }

    /** Pretty-print the entire table. */
    public synchronized String printTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== VEGETABLE PRICE TABLE ==========\n");
        if (table.isEmpty()) {
            sb.append("  (empty)\n");
        } else {
            for (VegetablePrice vp : table.values()) {
                sb.append("  ").append(vp).append("\n");
            }
        }
        sb.append("===========================================\n");
        return sb.toString();
    }
}
