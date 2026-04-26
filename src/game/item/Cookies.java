package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.action.Consumable;
import game.action.ConsumeAction;

/**
 * A pack of 5 cookies found on the moon's facility. Weighs 2 units.
 * Each cookie permanently decreases the consumer's maximum HP by 1 if eaten without
 * a {@link SterilisationBox}, or heals them for 1 HP if they carry one.
 * The item disappears from the inventory only after all 5 cookies have been eaten.
 */
public class Cookies extends Item implements Consumable {

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
     * Eats one cookie. Permanently reduces max HP by 1, or heals 1 HP with a SterilisationBox.
     * Removes the pack from the inventory once all cookies are consumed.
     *
     * @param actor the actor consuming the cookie
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String consume(Actor actor, GameMap map) {
        consume();

        boolean hasSterilisationBox = actor.getInventory().getItems().stream()
                .anyMatch(item -> item instanceof SterilisationBox);

        String result;
        if (hasSterilisationBox) {
            actor.heal(1);
            result = actor + " eats a sterilised cookie and recovers 1 HP.";
        } else {
            actor.modifyStatisticMaximum(
                    edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH,
                    edu.monash.fit2099.engine.statistics.StatisticOperations.DECREASE, 1);
            result = actor + " eats a stale cookie — maximum HP permanently reduced by 1.";
        }

        if (isDepleted()) {
            actor.getInventory().remove(this);
            result += " The empty cookie pack is discarded.";
        }
        return result;
    }

    @Override
    public String consumeMenuDescription(Actor actor) {
        return actor + " eats a Cookie (" + cookiesRemaining + " remaining)";
    }

    /**
     * Exposes a generic ConsumeAction when carried and cookies remain.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (!isDepleted()) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }
}