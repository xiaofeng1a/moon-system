package game.item;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;

/**
 * This item weighs 7 units and serves the critical function of sterilizing consumables —
 * including items and grounds found on the moon's facility.
 * Without it, you'd be licking alien residue off discarded cargo crates.
 * With it, you're a certified professional.
 */
public class SterilisationBox extends Item {

    public SterilisationBox() {
        super("Sterilisation Box", '▣');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(7));
        this.makePortable();
    }
}