package game.action;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An interface for items that can be consumed by an actor.
 * Any {@link edu.monash.fit2099.engine.items.Item} implementing this interface
 * automatically exposes a single ConsumeAction via its {@code allowableActions()},
 * eliminating the need to create a dedicated action class per consumable item.
 *
 * <p>Example implementors: Apple, Cookies, Flask
 */
public interface Consumable {

    /**
     * Defines what happens when this item is consumed by the given actor.
     * Implement this method to apply healing, poison, stat changes, or any
     * other effect relevant to the item.
     *
     * @param actor the actor consuming this item
     * @param map   the map the actor is currently on
     * @return a string describing the result of consumption
     */
    String consume(Actor actor, GameMap map);

    /**
     * Returns the label shown in the menu when the actor is offered this action.
     * By default, returns "consumes [item]". Override for a more specific description.
     *
     * @param actor the actor who would consume this item
     * @return a menu description string
     */
    default String consumeMenuDescription(Actor actor) {
        return actor + " consumes " + this;
    }
}
