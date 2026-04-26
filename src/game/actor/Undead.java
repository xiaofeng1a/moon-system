package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.behaviour.AttackBehaviour;
import game.behaviour.WanderBehaviour;
import game.inventory.BasicInventory;

/**
 * A hostile reanimated corpse of a former worker. Has 15 HP.
 *
 * <p>Behaviour priority:
 * <ol>
 *   <li>If a {@link ContractedWorker} is adjacent, attack them.</li>
 *   <li>Otherwise, wander randomly.</li>
 * </ol>
 *
 * <p>Uses shared  AttackBehaviour and WanderBehaviour named classes
 * since future enemies will reuse the same logic.
 * Intrinsic weapon is inlined as an anonymous subclass — only Undead uses it.
 */
public class Undead extends Actor {

    /** Targets only ContractedWorker — ignores all other creatures. */
    private final AttackBehaviour attackBehaviour = new AttackBehaviour(ContractedWorker.class);
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();

    /**
     * Constructor. Intrinsic weapon inlined — 1 damage, 10% hit rate.
     */
    public Undead() {
        super("Undead", 'U', 15, new BasicInventory());
        this.setIntrinsicWeapon(new IntrinsicWeapon(1, "punches", 10, "Fist") {});
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            return new DoNothingAction();
        }

        Location location = map.locationOf(this);

        // Priority 1: attack adjacent worker
        Action attack = attackBehaviour.operate(this, location);
        if (attack != null) return attack;

        // Priority 2: wander randomly
        Action wander = wanderBehaviour.operate(this, location);
        if (wander != null) return wander;

        return new DoNothingAction();
    }

    @Override
    public String unconscious(Actor otherActor, GameMap map) {
        map.removeActor(this);
        return this + " has been put to rest by " + otherActor;
    }
}