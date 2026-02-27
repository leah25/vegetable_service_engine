package common;

import java.io.Serializable;

/**
 * VegetablePrice - model object representing one row in the price table.
 * Must be Serializable so it can travel over RMI.
 *
 * DIARY: Day 1 - Created the data model.
 *        Fields: id, name, pricePerKg (price in KES per kilogram).
 */
public class VegetablePrice implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;       // unique identifier e.g. "V001"
    private String name;     // vegetable name e.g. "Tomato"
    private double pricePerKg; // price in KES per kg

    public VegetablePrice(String id, String name, double pricePerKg) {
        this.id = id;
        this.name = name;
        this.pricePerKg = pricePerKg;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public String getId()                        { return id; }
    public void   setId(String id)               { this.id = id; }

    public String getName()                      { return name; }
    public void   setName(String name)           { this.name = name; }

    public double getPricePerKg()                { return pricePerKg; }
    public void   setPricePerKg(double price)    { this.pricePerKg = price; }

    @Override
    public String toString() {
        return String.format("ID: %-6s | Name: %-15s | Price: KES %.2f/kg", id, name, pricePerKg);
    }
}
