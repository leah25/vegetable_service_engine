package client;

import common.Task;
import common.VegetablePrice;
import server.VegetablePriceTable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * CalculateCost - Task 5.
 * Prints a full receipt for the transaction showing:
 *   - Each item purchased (name, qty, unit price, line total)
 *   - Grand total cost
 *   - Amount given by customer
 *   - Change due
 *   - Cashier/clerk name logged into the machine
 *   - Date and time of transaction
 *
 * DIARY: Day 3 - Implemented the receipt task.
 *        Used a Map<String, Double> to hold vegetableId -> quantityKg
 *        so multiple items can appear on one receipt.
 *        The cashier name is passed in the constructor so each
 *        terminal can identify who processed the transaction.
 */
public class CalculateCost implements Task<String> {

    private static final long serialVersionUID = 1L;

    /** Items in this transaction: vegetableId -> quantityKg */
    private final Map<String, Double> items;

    /** Amount of cash the customer hands over */
    private final double amountGiven;

    /** Name of the cashier logged in */
    private final String cashierName;

    /**
     * @param items       map of vegetableId to quantity in kg
     * @param amountGiven cash given by the customer (KES)
     * @param cashierName name of logged-in cashier
     */
    public CalculateCost(Map<String, Double> items,
                         double amountGiven,
                         String cashierName) {
        this.items        = items;
        this.amountGiven  = amountGiven;
        this.cashierName  = cashierName;
    }

    /**
     * Execute: build and return a formatted receipt.
     * @return full receipt as a String
     */
    @Override
    public String execute() {
        VegetablePriceTable table = VegetablePriceTable.getInstance();

        StringBuilder sb = new StringBuilder();
        String divider  = "================================================\n";
        String divider2 = "------------------------------------------------\n";

        // Header
        sb.append(divider);
        sb.append("         VEGETABLE MARKET - RECEIPT\n");
        sb.append(divider);
        sb.append(String.format("  Date    : %s%n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
        sb.append(String.format("  Cashier : %s%n", cashierName));
        sb.append(divider2);
        sb.append(String.format("  %-15s %6s %10s %12s%n",
                "Item", "Qty(kg)", "Unit(KES)", "Total(KES)"));
        sb.append(divider2);

        double grandTotal = 0.0;
        boolean hasError  = false;

        for (Map.Entry<String, Double> entry : items.entrySet()) {
            String vegId  = entry.getKey();
            double qty    = entry.getValue();

            VegetablePrice vp = table.find(vegId);
            if (vp == null) {
                sb.append(String.format("  %-15s  NOT FOUND (ID: %s)%n", "???", vegId));
                hasError = true;
                continue;
            }

            double lineTotal = vp.getPricePerKg() * qty;
            grandTotal      += lineTotal;

            sb.append(String.format("  %-15s %6.2f %10.2f %12.2f%n",
                    vp.getName(), qty, vp.getPricePerKg(), lineTotal));
        }

        sb.append(divider2);
        sb.append(String.format("  %-33s %12.2f%n", "TOTAL (KES):", grandTotal));

        if (amountGiven < grandTotal) {
            sb.append(String.format("  %-33s %12.2f%n", "Amount Given (KES):", amountGiven));
            sb.append(String.format("  %-33s %12.2f%n", "INSUFFICIENT FUNDS - SHORT:", grandTotal - amountGiven));
        } else {
            double change = amountGiven - grandTotal;
            sb.append(String.format("  %-33s %12.2f%n", "Amount Given (KES):", amountGiven));
            sb.append(String.format("  %-33s %12.2f%n", "Change Due  (KES):", change));
        }

        sb.append(divider);
        sb.append("         Thank you for shopping with us!\n");
        sb.append(divider);

        if (hasError) {
            sb.append("  WARNING: Some items could not be found in the table.\n");
        }

        return sb.toString();
    }
}
