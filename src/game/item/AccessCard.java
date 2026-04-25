package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.Unlockable;
import game.action.UnlockAction;
import game.ground.Door;

/**
 * A class representing a small rectangular piece of plastic that holds entirely
 * too much power over your ability to walk through doors.
 * Weighs 1 unit.
 *
 * <p>When carried, checks adjacent locations for any ground implementing Unlockable
 * and offers a shared UnlockAction for each one found. This means AccessCard
 * automatically works with any future Unlockable ground, not just Door.
 *
 * @author Adrian Kristanto
 */
public class AccessCard extends Item {

    public AccessCard() {
        super("Access Card", 'A');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
    }

    /**
     * When carried, checks all adjacent locations for ground that implements {@link Unlockable}.
     * Returns an UnlockAction for each one that is not yet unlocked.
     *
     * @param owner the actor carrying the access card
     * @param map   the current game map
     * @return an ActionList containing an UnlockAction per adjacent unlockable ground
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        Location location = map.locationOf(owner);
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            Unlockable unlockable = destination.getGroundAs(Unlockable.class);
            if (unlockable != null) {
                if (destination.getGround() instanceof Door door) {
                    door.setLocation(destination);
                }
                actions.add(new UnlockAction(unlockable));
            }
        }
        return actions;
    }
}