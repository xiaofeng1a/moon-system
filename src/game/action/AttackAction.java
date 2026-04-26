package game.action;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An action that attacks a target actor using the attacker's intrinsic weapon.
 * If the target becomes unconscious after the attack, the target's
 * {@link Actor#unconscious(Actor, GameMap)} method is called.
 */
public class AttackAction extends Action {

    /** The target of the attack. */
    private final Actor target;

    /**
     * Constructor.
     *
     * @param target the actor to attack
     */
    public AttackAction(Actor target) {
        this.target = target;
    }

    /**
     * Performs the attack using the attacker's intrinsic weapon.
     * If the target falls unconscious, removes them from the map.
     *
     * @param actor The actor performing the attack.
     * @param map   The map the actor is on.
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = actor.getIntrinsicWeapon().attack(actor, target, map);
        if (!target.isConscious()) {
            result += "\n" + target.unconscious(actor, map);
        }
        return result;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " attacks " + target;
    }
}