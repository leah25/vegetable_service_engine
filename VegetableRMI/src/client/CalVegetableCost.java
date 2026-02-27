package client;

import common.Task;
import common.VegetablePrice;
import server.VegetablePriceTable;

/**
 * CalVegetableCost - Task 4.
 * Queries the vegetable price table for a given vegetable ID,
 * then calculates the total cost for the requested quantity (in kg).
 *
 * Returns a formatted cost summary string.
 *
 * DIARY: Day 3 - Implemented the cost calculation task.
 *        The task carries the vegetable ID and quantity.
 *        execute() looks up the price per kg, multiplies by quantity,
 *        and returns the breakdown as a formatted string.
 */
public class CalVegetableCost implements Task<String> {

    private static final long serialVersionUID = 1L;

    private final String vegetableId; // which vegetable to price
    private final double quantityKg;  // how many kg the client wants

    /**
     * @param vegetableId the vegetable to look up
     * @param quantityKg  quantity in kilograms
     */
    public CalVegetableCost(String vegetableId, double quantityKg) {
        this.vegetableId = vegetableId;
        this.quantityKg  = quantityKg;
    }

    /**
     * Execute: look up the price and compute total cost.
     * @return formatted cost breakdown string
     */
    @Override
    public String execute() {
        VegetablePriceTable table = VegetablePriceTable.getInstance();
        VegetablePrice vp = table.find(vegetableId);

        if (vp == null) {
            return "FAILED: No vegetable found with ID '" + vegetableId + "'.";
        }

        double totalCost = vp.getPricePerKg() * quantityKg;

        return String.format(
                "COST CALCULATION:\n"
                + "  Vegetable  : %s (ID: %s)\n"
                + "  Unit Price : KES %.2f per kg\n"
                + "  Quantity   : %.2f kg\n"
                + "  ------------------------------------\n"
                + "  TOTAL COST : KES %.2f",
                vp.getName(), vp.getId(),
                vp.getPricePerKg(),
                quantityKg,
                totalCost
        );
    }

    /** Getter so CalculateCost can reuse the computed total. */
    public double computeTotal() {
        VegetablePriceTable table = VegetablePriceTable.getInstance();
        VegetablePrice vp = table.find(vegetableId);
        if (vp == null) return 0.0;
        return vp.getPricePerKg() * quantityKg;
    }
}
