package client;

import common.Task;
import common.VegetablePrice;
import server.VegetablePriceTable;

/**
 * AddVegetablePrice - Task 1.
 * Adds a new vegetable-price entity to the price table.
 *
 * DIARY: Day 2 - Implemented the add task.
 *        The task holds the new VegetablePrice object as a field.
 *        When execute() is called on the server, it delegates to
 *        VegetablePriceTable.add() and returns a status message.
 */
public class AddVegetablePrice implements Task<String> {

    private static final long serialVersionUID = 1L;

    private final VegetablePrice vegetable; // the new vegetable to add

    /**
     * @param vegetable the new VegetablePrice to insert into the table
     */
    public AddVegetablePrice(VegetablePrice vegetable) {
        this.vegetable = vegetable;
    }

    /**
     * Execute: insert the vegetable into the shared price table.
     * @return success or failure message
     */
    @Override
    public String execute() {
        VegetablePriceTable table = VegetablePriceTable.getInstance();
        boolean added = table.add(vegetable);

        if (added) {
            return "SUCCESS: Added vegetable -> " + vegetable.toString();
        } else {
            return "FAILED: Vegetable with ID '" + vegetable.getId()
                    + "' already exists. Use Update instead.";
        }
    }
}
