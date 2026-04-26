package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.action.AttackAction;

/**
 * A behaviour that makes an actor attack an adjacent actor of a specified type.
 * Shared by any hostile creature — currently Undead.
 * The target type is injected at construction, making this reusable for any
 * future enemy regardless of what actor type they target.
 *
 * <p>Examples:
 * <pre>
 *   new AttackBehaviour(ContractedWorker.class) // Undead — workers only
 *   new AttackBehaviour(Actor.class)            // future enemy — any actor
 * </pre>
 */
public class AttackBehaviour implements Behaviour<Actor, Action> {

    /** The type of actor this behavior will target. */
    private final Class<? extends Actor> targetType;

    /**
     * Constructor.
     *
     * @param targetType the class of actor to target
     */
    public AttackBehaviour(Class<? extends Actor> targetType) {
        this.targetType = targetType;
    }

    /**
     * Scans all adjacent locations for an actor matching the target type.
     * Returns an AttackAction for the first match found, or null if none.
     *
     * @param actor    the actor performing the behavior
     * @param location the actor's current location
     * @return an AttackAction if a matching target is adjacent, otherwise null
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Actor target = exit.getDestination().getActor();
            if (target != null && targetType.isInstance(target)) {
                return new AttackAction(target);
            }
        }
        return null;
    }
}