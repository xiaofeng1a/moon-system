package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.action.UseFirstAidKitAction;

/**
 * A super useful medical kit, weighing a back-breaking 25 units.
 * When used, it permanently increases the carrying worker's maximum health by 1 point
 * and immediately restores their health to full.
 * It requires a 20-turn cooldown between uses — crucially, the timer only advances
 * while the kit is actively being carried by a worker. Leave it on the floor, stop the clock.
 */
public class FirstAidKit extends Item {

    /** Turns remaining before the kit can be used again. Zero means ready. */
    private int cooldownRemaining = 0;

    /** The number of turns required between uses. */
    private static final int COOLDOWN_TURNS = 20;

    public FirstAidKit() {
        super("First Aid Kit", '+');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(25));
        this.makePortable();
    }

    /**
     * Returns true if the kit is ready to use (cooldown has expired).
     *
     * @return true if cooldown is zero
     */
    public boolean isReady() {
        return cooldownRemaining == 0;
    }

    /**
     * Triggers the cooldown after the kit is used.
     */
    public void triggerCooldown() {
        cooldownRemaining = COOLDOWN_TURNS;
    }

    /**
     * Advances the cooldown timer by one tick.
     * This is only called by {@link #tick(Location, Actor)} while the kit is carried,
     * meaning the timer pauses when the kit is on the ground.
     */
    private void tickCooldown() {
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
    }

    /**
     * Called each turn while this item is being carried.
     * Advances the cooldown timer.
     *
     * @param currentLocation the location of the actor carrying this item
     * @param actor           the actor carrying this item
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        tickCooldown();
    }

    /**
     * When carried, offers a UseFirstAidKitAction if the kit is not on cooldown.
     *
     * @param owner the actor carrying this item
     * @param map   the current game map
     * @return an ActionList containing the use action if ready, otherwise empty
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (isReady()) {
            actions.add(new UseFirstAidKitAction(this));
        }
        return actions;
    }
}