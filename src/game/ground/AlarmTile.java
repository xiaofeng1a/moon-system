package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.ContractedWorker;
import game.alarm.AlarmSystem;

/**
 * A pressure-sensitive floor tile that silently triggers the facility alarm
 * the moment a ContractedWorker steps on it.
 *
 * <p>Looks identical to a normal floor tile from the outside — workers have
 * no way to distinguish it until it's too late.
 *
 * <p>SOLID — Single Responsibility: AlarmTile only handles detection and
 * triggering. What happens after the alarm is triggered is entirely the
 * responsibility of AlarmSystem and itsAlarmListener.
 *
 * <p>SOLID — Open/Closed: adding new alarm triggers (e.g. a motion sensor
 * creature, a tripwire item) does not require modifying this class or
 * AlarmSystem— just call {@code AlarmSystem.getInstance().trigger()}.
 */
public class AlarmTile extends Ground {

    /** Whether this tile has already triggered the alarm. */
    private boolean triggered = false;

    public AlarmTile() {
        super('*', "Alarm Tile");
    }

    /**
     * Each turn, checks if a ContractedWorker is standing on this tile.
     * If so, triggers the alarm via AlarmSystem (once only).
     *
     * @param location the location of this tile
     */
    @Override
    public void tick(Location location) {
        if (!triggered && location.containsAnActor()) {
            Actor actor = location.getActor();
            if (actor instanceof ContractedWorker) {
                triggered = true;
                AlarmSystem.getInstance().trigger();
            }
        }
    }
}