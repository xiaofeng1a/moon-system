package game.action;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A generic action for consuming any item that implements {@link Consumable}.
 * This eliminates the need to create a dedicated action class for each consumable item.
 * The actual consumption logic is fully delegated to the item via {@link Consumable#consume(Actor, GameMap)}.
 *
 * <p>Usage: in any {@link Consumable} item's {@code allowableActions()}, simply do:
 * <pre>
 *     actions.add(new ConsumeAction(this));
 * </pre>
 */
public class ConsumeAction extends Action {

    /** The consumable item to be consumed. */
    private final Consumable consumable;

    /**
     * Constructor.
     *
     * @param consumable the item to consume
     */
    public ConsumeAction(Consumable consumable) {
        this.consumable = consumable;
    }

    /**
     * Delegates the consumption logic entirely to the item.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return consumable.consume(actor, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return consumable.consumeMenuDescription(actor);
    }
}