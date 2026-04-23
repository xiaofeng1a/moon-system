package game.inventory;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import game.item.ItemStatistics;

/**
 * At its core, this is just an oversized {@code ArrayList}.
 * It prevents the player from simply carrying the entire {@code EclipseNebula}
 * with them by enforcing an arbitrary capacity limit.
 *
 * @author Adrian Kristanto
 */
public class WeightLimitedInventory extends Inventory {
    private int weightLimit;
    private int weight;

    public WeightLimitedInventory(int weightLimit) {
        this.weightLimit = weightLimit;
        this.weight = 0;
    }

    /**
     * Perform additional check when adding an item, making sure that by adding the item, the weight limit of the
     * inventory is not exceeded.
     *
     * @param item The Item to add.
     * @return true if the item is successfully added, false otherwise.
     */
    @Override
    public boolean add(Item item) {
        Display display = new Display();
        if (item.hasStatistic(ItemStatistics.WEIGHT)) {
            int itemWeight =  item.getStatistic(ItemStatistics.WEIGHT);
            if (weight + itemWeight < this.weightLimit) {
                items.add(item);
                this.weight += itemWeight;
                display.println(String.format("%s added successfully. Current inventory weight (%d/%d)", item, weight, weightLimit));
                return true;
            } else {
                display.println(String.format("Fails to add %s with weight %d. Weight limit will be exceeded (%d/%d)", item, itemWeight, weight + itemWeight, weightLimit));
                return false;
            }
        } else {
            String msg = String.format("%s does not have a weight statistic", item);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Remove the item from the inventory and update the inventory weight accordingly.
     *
     * @param item The Item to remove.
     * @return true if the item is successfully removed, false otherwise.
     */
    @Override
    public boolean remove(Item item) {
        if (item.hasStatistic(ItemStatistics.WEIGHT)) {
            int itemWeight =  item.getStatistic(ItemStatistics.WEIGHT);
            items.remove(item);
            this.weight -= itemWeight;
            return true;
        } else {
            String msg = String.format("%s does not have a weight statistic", item);
            throw new IllegalArgumentException(msg);
        }
    }
}
