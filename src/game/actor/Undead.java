package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.alarm.AlarmListener;
import game.alarm.AlarmSystem;
import game.behaviour.AttackBehaviour;
import game.behaviour.ChaseBehaviour;
import game.behaviour.WanderBehaviour;
import game.inventory.BasicInventory;

/**
 * A hostile reanimated corpse of a former worker. Has 15 HP.
 *
 * <p>Normal behavior priority:
 * <ol>
 *   <li>Attack adjacent {@link ContractedWorker}.</li>
 *   <li>Wander randomly.</li>
 * </ol>
 *
 * <p>When the alarm is triggered via {@link AlarmSystem}, the Undead overrides
 * its wander behaviour and actively chases the nearest worker using BFS.
 *
 * <p>Implements {@link AlarmListener} — registered with {@link AlarmSystem}
 * at construction. Deregisters itself when killed.
 *
 * <p>SOLID — Open/Closed: alarm behaviour is added by implementing AlarmListener,
 * not by modifying playTurn logic with if/else alarm checks.
 *
 * <p>SOLID — Dependency Inversion: Undead depends on the AlarmListener abstraction
 * and Behaviour abstraction, not on concrete alarm or pathfinding implementations.
 */
public class Undead extends Actor implements AlarmListener {

    /** Current movement behaviour — swapped when alarm triggers. */
    private Behaviour<Actor, Action> movementBehaviour;

    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private final ChaseBehaviour chaseBehaviour = new ChaseBehaviour(ContractedWorker.class);
    private final AttackBehaviour attackBehaviour = new AttackBehaviour(ContractedWorker.class);

    /**
     * Constructor. Registers with AlarmSystem so this Undead reacts to alarm events.
     * Intrinsic weapon inlined — 1 damage, 10% hit rate.
     */
    public Undead() {
        super("Undead", 'U', 15, new BasicInventory());
        this.setIntrinsicWeapon(new IntrinsicWeapon(1, "punches", 10, "Fist") {});
        this.movementBehaviour = wanderBehaviour; // default: wander
        AlarmSystem.getInstance().registerListener(this);
    }

    /**
     * On each turn, attack an adjacent worker if possible, then move using
     * the current movement behaviour (wander or chase depending on alarm state).
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            return new DoNothingAction();
        }

        Location location = map.locationOf(this);

        // Priority 1: attack adjacent worker
        Action attack = attackBehaviour.operate(this, location);
        if (attack != null) return attack;

        // Priority 2: move (wander or chase depending on alarm state)
        Action move = movementBehaviour.operate(this, location);
        if (move != null) return move;

        return new DoNothingAction();
    }

    /**
     * When the alarm triggers, switch from wandering to actively chasing workers.
     */
    @Override
    public void onAlarmTriggered() {
        this.movementBehaviour = chaseBehaviour;
    }

    /**
     * When the alarm deactivates, revert to normal wandering.
     */
    @Override
    public void onAlarmDeactivated() {
        this.movementBehaviour = wanderBehaviour;
    }

    /**
     * When killed, deregister from AlarmSystem to avoid memory leaks and
     * stale listener callbacks.
     */
    @Override
    public String unconscious(Actor otherActor, GameMap map) {
        AlarmSystem.getInstance().deregisterListener(this);
        map.removeActor(this);
        return this + " has been put to rest by " + otherActor;
    }
}