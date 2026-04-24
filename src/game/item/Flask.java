package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.action.Consumable;
import game.action.ConsumeAction;

/**
 * Due to severe budget cuts, the flask is only permitted to hold five (5)
 * mouthfuls of liquid per deployment. Employees are reminded not to consume
 * all five charges in a panic during a single encounter.
 * Once depleted, the empty flask remains in the worker's inventory as a
 * reminder of their poor resource management.
 */
public class Flask extends Item implements Consumable {
    static final int MAX_USES = 5;
    private int totalUsable = MAX_USES;

    public Flask() {
        super("Flask", 'u');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(3));
    }

    /**
     * @return true if the flask has no uses remaining
     */
    public boolean isDepleted() {
        return totalUsable <= 0;
    }

    /**
     * Decrease the flask's remaining uses by one.
     * Does nothing if the flask is already depleted.
     */
    public void consume() {
        if (!isDepleted()) {
            totalUsable--;
        }
    }

    /**
     * Consumes one use of the flask and heals the actor by 1 HP.
     *
     * @param actor the actor consuming the flask
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String consume(Actor actor, GameMap map) {
        consume();
        actor.heal(1);
        return actor + " drinks from the flask, restoring 1 point of health.";
    }

    @Override
    public String consumeMenuDescription(Actor actor) {
        return actor + " consumes Flask";
    }

    /**
     * When carried, exposes a generic ConsumeAction if the flask still has uses remaining.
     *
     * @param owner the actor carrying this flask
     * @param map   the current game map
     * @return an ActionList containing a ConsumeAction if not depleted, otherwise empty
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