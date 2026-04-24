package game.item;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;

/**
 * A piece of ancient technology. Extremely lightweight at just 1 unit.
 * Probably contains critical corporate data, or someone's unfinished novel.
 * Either way, it's worth picking up.
 */
public class FloppyDisk extends Item {

    public FloppyDisk() {
        super("Floppy Disk", '⊟');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
    }
}
