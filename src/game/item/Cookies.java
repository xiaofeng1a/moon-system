package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.action.EatCookieAction;

/**
 * A pack of 5 cookies found on the moon's facility. Weighs 2 units.
 * Each cookie permanently decreases the consumer's maximum HP by 1 if eaten without
 * a {@link SterilisationBox}, or heals them for 1 HP if they carry one.
 * The item disappears from the inventory only after all 5 cookies have been eaten.
 */
public class Cookies extends Item {

    /** Number of cookies remaining in the pack. */
    private int cookiesRemaining;

    private static final int MAX_COOKIES = 5;

    public Cookies() {
        super("Cookies", '◍');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(2));
        this.makePortable();
        this.cookiesRemaining = MAX_COOKIES;
    }

    /**
     * @return the number of cookies remaining
     */
    public int getCookiesRemaining() {
        return cookiesRemaining;
    }

    /**
     * Consumes one cookie, decrementing the count.
     */
    public void consume() {
        if (cookiesRemaining > 0) {
            cookiesRemaining--;
        }
    }

    /**
     * @return true if all cookies have been eaten
     */
    public boolean isDepleted() {
        return cookiesRemaining <= 0;
    }

    /**
     * When carried, exposes an EatCookieAction if cookies remain.
     *
     * @param owner the actor carrying this item
     * @param map   the current game map
     * @return an ActionList containing the eat action
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (!isDepleted()) {
            actions.add(new EatCookieAction(this));
        }
        return actions;
    }
}