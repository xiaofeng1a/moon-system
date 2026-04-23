package game.item;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ground.Fire;

import java.util.Random;

/**
 * An unstable light source found on the moon's facility. Weighs 7 units.
 * Starts with 10 units of oil fuel. While being carried, there is a 5% chance
 * each turn that it leaks and ignites the ground beneath the actor, consuming
 * 1 unit of oil and spawning a Fire tile on that location.
 * Once the oil runs out, the lantern can no longer start fires.
 */
public class Lantern extends Item {

    /** Current oil fuel remaining. */
    private int oilRemaining;

    private static final int MAX_OIL = 10;
    private static final int LEAK_CHANCE_PERCENT = 5;

    private final Random random = new Random();

    public Lantern() {
        super("Lantern", '&');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(7));
        this.makePortable();
        this.oilRemaining = MAX_OIL;
    }

    /**
     * Each turn while carried, rolls a 5% chance to leak.
     * On a leak, reduces oil by 1 and spawns a Fire on the actor's current tile.
     *
     * @param currentLocation the location of the actor carrying this item
     * @param actor           the actor carrying this item
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        if (oilRemaining > 0 && random.nextInt(100) < LEAK_CHANCE_PERCENT) {
            oilRemaining--;
            currentLocation.setGround(new Fire());
        }
    }
}