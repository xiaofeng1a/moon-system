package game;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * The bureaucratic process of asking a piece of the environment for permission to pass.
 * Targets a specific {@link Door} at a known {@link Location}, avoiding any need to
 * re-scan the map at execution time.
 */
public class UnlockDoorAction extends Action {

    /** The door to unlock. */
    private final Door door;

    /** The location of the door. */
    private final Location doorLocation;

    /**
     * Constructor.
     *
     * @param door         the door to unlock
     * @param doorLocation the location of the door on the map
     */
    public UnlockDoorAction(Door door, Location doorLocation) {
        this.door = door;
        this.doorLocation = doorLocation;
    }

    /**
     * Unlocks the targeted door.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        door.isUnlocked = true;
        return actor + " unlocked " + door + " at " + doorLocation;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " unlocks door at " + doorLocation;
    }
}