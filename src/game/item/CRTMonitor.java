package game.item;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;

/**
 * A massive, incredibly heavy piece of archaic junk. Weighs 30 units.
 * Nobody knows why it's still here. Nobody knows why you'd want to carry it.
 * And yet, here we are.
 */
public class CRTMonitor extends Item {

    public CRTMonitor() {
        super("CRT Monitor", '◙');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(30));
        this.makePortable();
    }
}
