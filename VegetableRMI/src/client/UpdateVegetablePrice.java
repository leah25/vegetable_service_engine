package client;

import common.Task;
import common.VegetablePrice;
import server.VegetablePriceTable;

/**
 * UpdateVegetablePrice - Task 2.
 * Updates an existing vegetable-price entity in the price table.
 *
 * DIARY: Day 2 - Implemented the update task.
 *        The task carries the updated VegetablePrice object.
 *        execute() calls VegetablePriceTable.update() which replaces
 *        the existing entry if the ID is found.
 */
public class UpdateVegetablePrice implements Task<String> {

    private static final long serialVersionUID = 1L;

    private final VegetablePrice updatedVegetable; // updated data

    /**
     * @param updatedVegetable the vegetable with updated fields (ID must exist)
     */
    public UpdateVegetablePrice(VegetablePrice updatedVegetable) {
        this.updatedVegetable = updatedVegetable;
    }

    /**
     * Execute: replace the existing entry with the updated vegetable.
     * @return success or failure message
     */
    @Override
    public String execute() {
        VegetablePriceTable table = VegetablePriceTable.getInstance();

        // Check if vegetable exists before updating
        VegetablePrice existing = table.find(updatedVegetable.getId());
        if (existing == null) {
            return "FAILED: No vegetable found with ID '" + updatedVegetable.getId()
                    + "'. Use Add instead.";
        }

        String oldDetails = existing.toString();
        boolean updated = table.update(updatedVegetable);

        if (updated) {
            return "SUCCESS: Updated vegetable.\n"
                    + "  OLD: " + oldDetails + "\n"
                    + "  NEW: " + updatedVegetable.toString();
        } else {
            return "FAILED: Update operation failed for ID '" + updatedVegetable.getId() + "'.";
        }
    }
}
