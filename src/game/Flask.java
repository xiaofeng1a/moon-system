package game;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.DropAction;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;

/**
 * Due to severe budget cuts, the flask is only permitted to hold five (5)
 * mouthfuls of liquid per deployment. Employees are reminded not to consume
 * all five charges in a panic during a single encounter.
 * Once depleted, the empty flask remains in the worker's inventory as a
 * reminder of their poor resource management.
 */
public class Flask extends Item {
    static final int MAX_USES = 5;
    private int totalUsable = MAX_USES;

    public Flask() {
        super("Flask", 'u');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(3));
        this.makePortable();
    }

    /**
     * @return true if the flask has no uses remaining
     */
    public boolean isDepleted() {
        return totalUsable <= 0;
    }

    /**
     * Make sure the flask can't be dropped.
     */
    @Override
    public DropAction getDropAction(Actor actor) {
        return null; // flask can never be dropped
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
     * When carried, expose a ConsumeFlaskAction if the flask still has uses remaining.
     *
     * @param owner the actor carrying this flask
     * @param map   the current game map
     * @return an ActionList containing a ConsumeFlaskAction if not depleted, otherwise empty
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (!isDepleted()) {
            actions.add(new ConsumeFlaskAction(this));
        }
        return actions;
    }
}