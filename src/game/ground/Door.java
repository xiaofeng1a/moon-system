package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.Unlockable;
import game.alarm.AlarmListener;
import game.alarm.AlarmSystem;

/**
 * A door that blocks actor movement until unlocked by an AccessCard.
 *
 * <p>Implements {@link AlarmListener} — when the alarm triggers, the door
 * is immediately locked tight for {@value ALARM_LOCK_TURNS} turns, during
 * which the AccessCard cannot reopen it. The lock countdown ticks
 * each turn via {@link #onAlarmTick()}. Once expired, the door reverts to
 * its pre-alarm state.
 *
 * <p>SOLID — Single Responsibility: Door manages its own lock state.
 * AlarmSystem only calls the interface methods — it doesn't know Door exists.
 *
 * <p>SOLID — Open/Closed: Door's alarm response is added by implementing
 * AlarmListener, with no changes to AlarmSystem or any other class.
 */
public class Door extends Ground implements Unlockable, AlarmListener {

    /** Whether this door has been unlocked by an AccessCard. */
    boolean isUnlocked = false;

    /** Location of this door, set when placed on map. */
    private Location doorLocation;

    /** Number of turns doors stay locked after alarm triggers. */
    private static final int ALARM_LOCK_TURNS = 10;

    /** Remaining turns of alarm-forced lockdown. 0 = not in lockdown. */
    private int alarmLockRemaining = 0;

    public Door() {
        super('=', "Door");
        AlarmSystem.getInstance().registerListener(this);
    }

    /**
     * Sets the location context for informative unlock messages.
     *
     * @param location the location of this door
     */
    public void setLocation(Location location) {
        this.doorLocation = location;
    }

    /**
     * Actors can enter only if the door is unlocked AND not in alarm lockdown.
     *
     * @param actor the Actor to check
     * @return true if the door can be entered
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return isUnlocked && alarmLockRemaining == 0;
    }

    /**
     * Unlocks this door — only succeeds if not in alarm lockdown.
     *
     * @param actor the actor performing to unlock
     * @param map   the map the actor is on
     * @return a description of what happened
     */
    @Override
    public String unlock(Actor actor, GameMap map) {
        if (alarmLockRemaining > 0) {
            return actor + " cannot unlock " + this + " — alarm lockdown active for "
                    + alarmLockRemaining + " more turns!";
        }
        isUnlocked = true;
        String locationStr = doorLocation != null ? " at " + doorLocation : "";
        return actor + " unlocked " + this + locationStr;
    }

    @Override
    public String unlockMenuDescription(Actor actor) {
        if (alarmLockRemaining > 0) {
            return actor + " tries to unlock door (LOCKED — alarm active, " + alarmLockRemaining + " turns remaining)";
        }
        String locationStr = doorLocation != null ? " at " + doorLocation : "";
        return actor + " unlocks " + this + locationStr;
    }

    /**
     * When alarm triggers, immediately lock all doors for {@value ALARM_LOCK_TURNS} turns.
     */
    @Override
    public void onAlarmTriggered() {
        isUnlocked = false;
        alarmLockRemaining = ALARM_LOCK_TURNS;
    }

    /**
     * Each turn the alarm is active, count down the lockdown timer.
     * Once expired, doors can be reopened normally.
     */
    @Override
    public void onAlarmTick() {
        if (alarmLockRemaining > 0) {
            alarmLockRemaining--;
        }
    }

    /**
     * When alarm deactivates, clear any remaining lockdown.
     */
    @Override
    public void onAlarmDeactivated() {
        alarmLockRemaining = 0;
    }
}