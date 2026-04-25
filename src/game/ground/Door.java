package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.Unlockable;

/**
 * Its primary purpose in the universe is to halt the progress of underpaid
 * {@code ContractedWorker}s until they can produce the correct rectangular
 * piece of plastic.
 *
 * <p>Implements {@link Unlockable} so that the shared UnlockAction can
 * be used without a dedicated action class. Any future lockable ground or object
 * can follow the same pattern.
 */
public class Door extends Ground implements Unlockable {

    /** Whether this door has been unlocked. */
    boolean isUnlocked = false;

    /** The location of this door, set when placed on the map. */
    private Location doorLocation;

    public Door() {
        super('=', "Door");
    }

    /**
     * Sets the location of this door so it can be referenced in unlock messages.
     *
     * @param location the location of this door on the map
     */
    public void setLocation(Location location) {
        this.doorLocation = location;
    }

    /**
     * If the door is unlocked, any actor can step through it.
     *
     * @param actor the Actor to check
     * @return true if the door is unlocked, false otherwise
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return isUnlocked;
    }

    /**
     * Unlocks this door.
     *
     * @param actor the actor performing unlock
     * @param map   the map the actor is on
     * @return a description of what happened
     */
    @Override
    public String unlock(Actor actor, GameMap map) {
        isUnlocked = true;
        String locationStr = doorLocation != null ? " at " + doorLocation : "";
        return actor + " unlocked " + this + locationStr;
    }

    @Override
    public String unlockMenuDescription(Actor actor) {
        String locationStr = doorLocation != null ? " at " + doorLocation : "";
        return actor + " unlocks " + this + locationStr;
    }
}