package game.action;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.Unlockable;

/**
 * A generic action for unlocking any object that implements {@link Unlockable}.
 * This eliminates the need to create a dedicated action class for each lockable object.
 * The actual unlock logic is fully delegated to the object via {@link Unlockable#unlock(Actor, GameMap)}.
 *
 * <p>Usage: in any class that implements {@link Unlockable}, simply offer:
 * <pre>
 *     actions.add(new UnlockAction(this));
 * </pre>
 */
public class UnlockAction extends Action {

    /** The object to be unlocked. */
    private final Unlockable unlockable;

    /**
     * Constructor.
     *
     * @param unlockable the object to unlock
     */
    public UnlockAction(Unlockable unlockable) {
        this.unlockable = unlockable;
    }

    /**
     * Delegates the unlock logic entirely to the unlockable object.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return unlockable.unlock(actor, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return unlockable.unlockMenuDescription(actor);
    }
}