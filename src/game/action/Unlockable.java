package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An interface for objects that can be unlocked by an actor.
 * Any class implementing this interface automatically works with the shared
 * UnlockAction eliminating the need for a dedicated action class
 * per lockable object.
 *
 * <p>Example implementors: Door
 * <p>Future implementors: safes, cages, vaults, sealed hatches, etc.
 */
public interface Unlockable {

    /**
     * Defines what happens when this object is unlocked by the given actor.
     * Implement this to change state, grant access, trigger events, etc.
     *
     * @param actor the actor performing unlock
     * @param map   the map the actor is currently on
     * @return a string describing the result of unlock
     */
    String unlock(Actor actor, GameMap map);

    /**
     * Returns the label shown in the menu when the actor is offered this action.
     * By default, returns "unlocks [this]". Override for a more specific description.
     *
     * @param actor the actor who would perform unlock
     * @return a menu description string
     */
    default String unlockMenuDescription(Actor actor) {
        return actor + " unlocks " + this;
    }
}