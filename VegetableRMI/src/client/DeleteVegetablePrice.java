package client;

import common.Task;
import common.VegetablePrice;
import server.VegetablePriceTable;

/**
 * DeleteVegetablePrice - Task 3.
 * Deletes a vegetable-price entity from the price table by ID.
 *
 * DIARY: Day 2 - Implemented the delete task.
 *        Only the vegetable ID is needed to perform deletion.
 *        execute() calls VegetablePriceTable.delete() and returns
 *        a message confirming which vegetable was removed.
 */
public class DeleteVegetablePrice implements Task<String> {

    private static final long serialVersionUID = 1L;

    private final String vegetableId; // ID of the vegetable to delete

    /**
     * @param vegetableId the ID of the vegetable to remove
     */
    public DeleteVegetablePrice(String vegetableId) {
        this.vegetableId = vegetableId;
    }

    /**
     * Execute: remove the vegetable from the price table.
     * @return success or failure message
     */
    @Override
    public String execute() {
        VegetablePriceTable table = VegetablePriceTable.getInstance();

        // Retrieve the entry first so we can confirm what was deleted
        VegetablePrice existing = table.find(vegetableId);
        if (existing == null) {
            return "FAILED: No vegetable found with ID '" + vegetableId + "'.";
        }

        boolean deleted = table.delete(vegetableId);

        if (deleted) {
            return "SUCCESS: Deleted vegetable -> " + existing.toString();
        } else {
            return "FAILED: Could not delete vegetable with ID '" + vegetableId + "'.";
        }
    }
}
